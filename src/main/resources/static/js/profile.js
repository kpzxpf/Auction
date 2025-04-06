document.addEventListener('DOMContentLoaded', async () => {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        window.location.href = 'login.html';
        return;
    }

    const logoutButtons = [document.getElementById('logoutButton'), document.getElementById('logoutButtonFooter')];
    logoutButtons.forEach(button => {
        button.addEventListener('click', () => {
            localStorage.removeItem('userId');
            window.location.href = 'index.html';
        });
    });

    document.getElementById('add-lot-btn').href = `add-lot.html?seller_id=${userId}`;
    await loadUserProfile(userId);
    await loadUserLots(userId);
    updateNavigation();
});

async function loadUserProfile(userId) {
    try {
        const response = await fetch(`http://localhost:8080/user/${userId}`, {
            headers: {
                'X-User-Id': userId
            }
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
        const lotElement = document.createElement('div');
        lotElement.className = 'col-md-4 mb-4';
        lotElement.innerHTML = `
            <div class="card h-100 shadow-sm">
                <img src="${lot.images?.[0]?.url || 'images/banner.jpg'}" class="card-img-top" alt="${lot.title}" style="height: 200px; object-fit: cover;">
                <div class="card-body">
                    <h5 class="card-title">${lot.title}</h5>
                    <p class="card-text text-muted">${lot.description || ''}</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="badge bg-primary">${lot.categoryName || 'Без категории'}</span>
                        <h5 class="text-success">${lot.currentPrice.toFixed(2)}₽</h5>
                    </div>
                    <div class="mt-2">
                        <a href="lot.html?id=${lot.id}" class="btn btn-primary btn-sm"><i class="fas fa-eye"></i> Подробнее</a>
                    </div>
                </div>
                <div class="card-footer bg-transparent">
                    <small class="text-muted">Окончание: ${new Date(lot.endTime).toLocaleDateString()}</small>
                </div>
            </div>
        `;
        container.appendChild(lotElement);
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
}