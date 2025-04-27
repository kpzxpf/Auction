document.addEventListener('DOMContentLoaded', async () => {
    const params = new URLSearchParams(window.location.search);
    const lotId = params.get('id');
    const userId = localStorage.getItem('userId');

    if (!userId) {
        alert('Пожалуйста, войдите в систему!');
        window.location.href = 'login.html';
        return;
    }
    if (!lotId) {
        alert('Не указан идентификатор лота.');
        window.location.href = 'profile.html';
        return;
    }

    // проставляем hidden-поле sellerId
    document.getElementById('seller_id').value = userId;

    initCategoryModal();
    setupImagePreview();
    setupFormSubmission(lotId, userId);
    setupDeleteButton(lotId, userId);
    updateNavigation();

    // после инициализации остальных обработчиков – подгружаем данные
    await loadLotData(lotId, userId);
});

async function loadLotData(lotId, userId) {
    try {
        const resp = await fetch(`http://localhost:8080/lots/${lotId}`, {
            headers: { 'X-User-Id': userId }
        });
        if (!resp.ok) throw new Error('Лот не найден.');
        const lot = await resp.json();

        // заполняем форму
        document.getElementById('title').value = lot.title;
        document.getElementById('description').value = lot.description;
        document.getElementById('startingPrice').value = lot.startingPrice;
        document.getElementById('startTime').value = lot.startTime;
        document.getElementById('endTime').value = lot.endTime;
        document.getElementById('status').value = lot.status;

        // проставляем выбранную категорию
        document.getElementById('selected-category').value = lot.categoryName;
        document.getElementById('category_id').value = lot.categoryId;

        // показываем существующие изображения
        renderExistingImages(lot.imageUrls || []);
    } catch (err) {
        console.error(err);
        alert(err.message);
        window.location.href = 'profile.html';
    }
}

function renderExistingImages(urls) {
    const preview = document.getElementById('imagePreview');
    preview.innerHTML = '';
    urls.forEach(src => {
        const img = document.createElement('img');
        img.src = src;
        img.className = 'img-thumbnail m-1';
        img.style.width = '100px';
        img.style.height = '100px';
        img.style.objectFit = 'cover';
        preview.appendChild(img);
    });
}

function initCategoryModal() {
    const modalEl = document.getElementById('categoryModal');
    const userId = localStorage.getItem('userId');
    modalEl.addEventListener('show.bs.modal', async () => {
        try {
            const resp = await fetch('http://localhost:8080/categories', {
                headers: { 'X-User-Id': userId }
            });
            if (!resp.ok) throw new Error();
            const cats = await resp.json();
            const list = document.getElementById('categoryList');
            list.innerHTML = '';
            cats.forEach(c => {
                const li = document.createElement('li');
                li.className = 'list-group-item list-group-item-action';
                li.textContent = c.name;
                li.dataset.categoryId = c.id;
                list.appendChild(li);
            });
        } catch {
            alert('Не удалось загрузить категории');
        }
    });

    modalEl.addEventListener('click', event => {
        const t = event.target;
        if (t.tagName === 'LI' && t.dataset.categoryId) {
            document.getElementById('selected-category').value = t.textContent;
            document.getElementById('category_id').value = t.dataset.categoryId;
            bootstrap.Modal.getInstance(modalEl).hide();
        }
    });
}

function setupImagePreview() {
    const input = document.getElementById('images');
    const preview = document.getElementById('imagePreview');
    input.addEventListener('change', () => {
        preview.innerHTML = '';
        Array.from(input.files).forEach(file => {
            const reader = new FileReader();
            reader.onload = e => {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.className = 'img-thumbnail m-1';
                img.style.width = '100px';
                img.style.height = '100px';
                img.style.objectFit = 'cover';
                preview.appendChild(img);
            };
            reader.readAsDataURL(file);
        });
    });
}

function restrictFileCount(input, max) {
    if (input.files.length > max) {
        alert(`Максимум ${max} изображений!`);
        input.value = '';
        document.getElementById('imagePreview').innerHTML = '';
    }
}

function setupFormSubmission(lotId, userId) {
    const form = document.getElementById('edit-lot-form');
    form.addEventListener('submit', async e => {
        e.preventDefault();

        // Валидация дат
        const startTime = new Date(form.startTime.value);
        const endTime   = new Date(form.endTime.value);
        const now       = new Date();
        if (startTime < now) {
            alert('Время начала не может быть в прошлом');
            return;
        }
        if (startTime >= endTime) {
            alert('Время окончания должно быть позже начала');
            return;
        }

        // Собираем объект LotDto
        const lot = {
            id: lotId,
            title: form.title.value,
            description: form.description.value,
            startingPrice: parseFloat(form.startingPrice.value),
            startTime: form.startTime.value,
            endTime: form.endTime.value,
            sellerId: userId,
            categoryId: form.categoryId.value,
            categoryName: form.categoryName.value,
            status: form.status.value
        };

        // Создаём multipart/form-data
        const formData = new FormData();
        // Важное: часть 'lot' должна быть JSON
        formData.append(
            'lot',
            new Blob([JSON.stringify(lot)], { type: 'application/json' })
        );

        // Добавляем файлы (они попадут в @RequestPart("files"))
        const files = document.getElementById('images').files;
        Array.from(files).forEach(file => {
            formData.append('files', file);
        });

        try {
            const resp = await fetch('http://localhost:8080/lots', {
                method: 'PUT',
                headers: { 'X-User-Id': userId },
                body: formData
            });
            if (!resp.ok) throw new Error('Ошибка при обновлении лота');
            alert('Лот успешно обновлен!');
            window.location.href = 'profile.html';
        } catch (err) {
            console.error(err);
            alert(err.message);
        }
    });
}
function setupDeleteButton(lotId, userId) {
    const deleteButton = document.getElementById('delete-lot-btn');
    deleteButton.addEventListener('click', async () => {
        if (!confirm('Вы уверены, что хотите удалить этот лот? Это действие нельзя отменить.')) {
            return;
        }
        try {
            const resp = await fetch(`http://localhost:8080/lots/${lotId}`, {
                method: 'DELETE',
                headers: {
                    'X-User-Id': userId
                }
            });
            if (!resp.ok) {
                throw new Error('Ошибка при удалении лота');
            }
            alert('Лот успешно удалён.');
            window.location.href = 'profile.html';
        } catch (err) {
            console.error(err);
            alert(err.message);
        }
    });
}

function updateNavigation() {
    const userId = localStorage.getItem('userId');
    document.getElementById('profileLink').style.display = userId ? 'block' : 'none';
    document.getElementById('loginLink').style.display = userId ? 'none' : 'block';
    document.getElementById('registerLink').style.display = userId ? 'none' : 'block';
}
