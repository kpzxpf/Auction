document.addEventListener('DOMContentLoaded', () => {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        alert('Пожалуйста, войдите в систему!');
        window.location.href = 'login.html';
        return;
    }

    document.getElementById('seller_id').value = userId;
    initCategoryModal();
    setupImagePreview();
    setupFormSubmission(userId);
    updateNavigation();
});

function initCategoryModal() {
    const modal = document.getElementById('categoryModal');
    const userId = localStorage.getItem('userId');

    modal.addEventListener('show.bs.modal', async () => {
        try {
            const response = await fetch('http://localhost:8080/categories', {
                headers: { "X-User-Id": userId }
            });
            if (!response.ok) throw new Error('Ошибка загрузки категорий');
            const categories = await response.json();

            const categoryList = document.getElementById('categoryList');
            categoryList.innerHTML = '';
            categories.forEach(category => {
                const li = document.createElement('li');
                li.className = 'list-group-item list-group-item-action';
                li.textContent = category.name;
                li.dataset.categoryId = category.id;
                categoryList.appendChild(li);
            });
        } catch (error) {
            console.error('Ошибка:', error);
            alert('Не удалось загрузить категории');
        }
    });

    modal.addEventListener('click', (event) => {
        const target = event.target;
        if (target.tagName === 'LI' && target.classList.contains('list-group-item')) {
            document.getElementById('selected-category').value = target.textContent;
            document.getElementById('category_id').value = target.dataset.categoryId;
            bootstrap.Modal.getInstance(modal).hide();
        }
    });
}

function setupImagePreview() {
    const imageInput = document.getElementById('images');
    const previewContainer = document.getElementById('imagePreview');

    imageInput.addEventListener('change', function() {
        previewContainer.innerHTML = '';
        Array.from(this.files).forEach(file => {
            const reader = new FileReader();
            reader.onload = (e) => {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.className = 'img-thumbnail m-1';
                img.style.width = '100px';
                img.style.height = '100px';
                img.style.objectFit = 'cover';
                previewContainer.appendChild(img);
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

function setupFormSubmission(userId) {
    const form = document.getElementById('add-lot-form');
    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const categoryId = document.getElementById('category_id').value;
        if (!categoryId) {
            alert('Пожалуйста, выберите категорию');
            return;
        }

        const startTime = new Date(document.getElementById('startTime').value);
        const endTime = new Date(document.getElementById('endTime').value);
        const now = new Date();
        if (startTime < now) {
            alert('Время начала не может быть в прошлом');
            return;
        }
        if (startTime >= endTime) {
            alert('Время окончания должно быть позже времени начала');
            return;
        }

        const formData = new FormData(form);
        formData.set('categoryId', categoryId);
        formData.set('sellerId', userId);

        try {
            const response = await fetch('http://localhost:8080/lots', {
                method: 'POST',
                headers: { "X-User-Id": userId },
                body: formData
            });

            if (!response.ok) throw new Error('Ошибка создания лота');
            alert('Лот успешно создан!');
            window.location.href = 'profile.html';
        } catch (error) {
            console.error('Ошибка:', error);
            alert('Ошибка при создании лота');
        }
    });
}

function updateNavigation() {
    const userId = localStorage.getItem('userId');
    const profileLink = document.getElementById('profileLink');
    const logoutButton = document.getElementById('logoutButton');
    const loginLink = document.getElementById('loginLink');
    const registerLink = document.getElementById('registerLink');

    if (userId) {
        profileLink.style.display = 'block';
        logoutButton.style.display = 'block';
        loginLink.style.display = 'none';
        registerLink.style.display = 'none';
    } else {
        profileLink.style.display = 'none';
        logoutButton.style.display = 'none';
        loginLink.style.display = 'block';
        registerLink.style.display = 'block';
    }

    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('userId');
        window.location.href = 'login.html';
    });
}