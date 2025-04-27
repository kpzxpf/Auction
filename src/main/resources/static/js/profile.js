import {updateNavigation} from "./updateNavigation";

document.addEventListener('DOMContentLoaded', async () => {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        window.location.href = 'login.html';
        return;
    }

    // Кнопки выхода
    const logoutButtons = [
        document.getElementById('logoutButton'),
        document.getElementById('logoutButtonFooter')
    ];
    logoutButtons.forEach(button => {
        button.addEventListener('click', () => {
            localStorage.removeItem('userId');
            window.location.href = 'index.html';
        });
    });

    // Ссылка на добавление лота
    document.getElementById('add-lot-btn').href = `add-lot.html?seller_id=${userId}`;

    // Загрузка профиля и лотов
    await loadUserProfile(userId);
    await loadUserLots(userId);
    updateNavigation();

    // --- Код для пополнения баланса ---
    const topUpButton = document.getElementById('topUpButton');
    const confirmTopUp = document.getElementById('confirmTopUp');
    const topUpAmountInput = document.getElementById('topUpAmount');
    const topUpError = document.getElementById('topUpError');
    const topUpModalEl = document.getElementById('topUpModal');
    const topUpModal = new bootstrap.Modal(topUpModalEl);

    // Сброс ошибки и поля при открытии модала
    topUpButton.addEventListener('click', () => {
        topUpAmountInput.value = '';
        topUpError.style.display = 'none';
    });

    // Обработка подтверждения пополнения
    confirmTopUp.addEventListener('click', async () => {
        const amount = parseFloat(topUpAmountInput.value);
        if (isNaN(amount) || amount <= 0) {
            topUpError.textContent = 'Введите корректную сумму.';
            topUpError.style.display = 'block';
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/user/${userId}/top-up`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-User-Id': userId
                },
                body: JSON.stringify({ amount })
            });
            if (!response.ok) throw new Error('Сервер вернул ошибку');
            const updatedUser = await response.json();

            // Обновляем отображение баланса
            document.getElementById('profile-balance').textContent = `${updatedUser.balance.toFixed(2)}₽`;
            topUpModal.hide();
        } catch (err) {
            console.error(err);
            topUpError.textContent = 'Не удалось пополнить баланс. Попробуйте позже.';
            topUpError.style.display = 'block';
        }
    });
});

async function loadUserProfile(userId) {
    try {
        const response = await fetch(`http://localhost:8080/user/${userId}`, {
            headers: { 'X-User-Id': userId }
        });
        if (!response.ok) throw new Error('Ошибка загрузки профиля');
        const user = await response.json();

        document.getElementById('profile-username').textContent = user.username;
        document.getElementById('profile-email').textContent = user.email;
        document.getElementById('profile-balance').textContent = `${user.balance || 0}₽`;
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка при загрузке профиля');
    }
}

async function loadUserLots(userId) {
    try {
        const response = await fetch(`http://localhost:8080/lots/user/${userId}`, {
            headers: { 'X-User-Id': userId }
        });
        if (!response.ok) throw new Error('Ошибка загрузки лотов');
        const lots = await response.json();
        displayUserLots(lots);
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка при загрузке лотов');
    }
}

function displayUserLots(lots) {
    const container = document.getElementById('user-lots');
    container.innerHTML = '';

    if (lots.length === 0) {
        container.innerHTML = '<p class="text-center">Нет ваших лотов.</p>';
        return;
    }

    lots.forEach(lot => {
        const col = document.createElement('div');
        col.className = 'col-md-4 mb-4 fade-in';

        col.innerHTML = `
            <div class="card h-100 shadow-sm">
                <img src="${lot.imageUrls?.[0] || 'images/banner.jpg'}"
                     class="card-img-top"
                     alt="${lot.title}"
                     style="height: 200px; object-fit: cover;">
                <div class="card-body">
                    <h5 class="card-title">${lot.title}</h5>
                    <p class="card-text text-muted">${lot.description || ''}</p>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="badge bg-primary">${lot.categoryName || 'Без категории'}</span>
                        <h5 class="text-success">${lot.currentPrice.toFixed(2)}₽</h5>
                    </div>
                    <div class="d-flex">
                        <a href="lot.html?id=${lot.id}" class="btn btn-primary btn-sm me-2">
                            <i class="fas fa-eye"></i> Подробнее
                        </a>
                        <a href="edit-lot.html?id=${lot.id}" class="btn btn-secondary btn-sm">
                            <i class="fas fa-edit"></i> Редактировать
                        </a>
                    </div>
                </div>
                <div class="card-footer bg-transparent">
                    <small class="text-muted">
                        Окончание: ${new Date(lot.endTime).toLocaleDateString()}
                    </small>
                </div>
            </div>
        `;

        container.appendChild(col);
    });
}

