document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const sellerId = urlParams.get('seller_id');

    if (!sellerId) {
        alert('Ошибка авторизации!');
        window.location.href = 'profile.html';
        return;
    }

    document.getElementById('seller_id').value = sellerId;

    initCategoryModal();
    setupImagePreview();
    setupFormSubmission(sellerId);
});

function initCategoryModal() {
    const modal = document.getElementById('categoryModal');

    modal.addEventListener('show.bs.modal', async () => {
        try {
            const response = await fetch('http://localhost:8080/categories');
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
            console.error('Ошибка загрузки категорий:', error);
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

function setupFormSubmission(sellerId) {
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
        if (startTime >= endTime) {
            alert('Время окончания должно быть позже времени начала');
            return;
        }

        const formData = new FormData(form);
        formData.append('categoryId', categoryId); // Отправляем categoryId вместо categoryName

        try {
            const response = await fetch('http://localhost:8080/lots', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Ошибка сервера');
            }

            alert('Лот успешно создан!');
            window.location.href = `profile.html`;
        } catch (error) {
            console.error('Ошибка:', error);
            alert(`Ошибка при создании лота: ${error.message}`);
        }
    });
}