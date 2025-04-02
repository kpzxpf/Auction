document.addEventListener('DOMContentLoaded', () => {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        window.location.href = 'login.html';
        return;
    }

    loadUserBids(userId);
    updateNavigation();
});

async function loadUserBids(userId) {
    try {
        const response = await fetch('http://localhost:8080/bids/user', {
            headers: { 'X-User-Id': userId }
        });
        if (!response.ok) throw new Error('Ошибка загрузки ставок');
        const bids = await response.json();
        displayUserBids(bids);
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка при загрузке ставок');
    }
}

function displayUserBids(bids) {
    const container = document.getElementById('bids-list');
    container.innerHTML = '';
    if (bids.length === 0) {
        container.innerHTML = '<p class="text-center">Вы еще не сделали ставок.</p>';
        return;
    }

    bids.forEach(bid => {
        const bidElement = document.createElement('div');
        bidElement.className = 'col-md-4 mb-4';
        bidElement.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Лот: ${bid.lotTitle}</h5>
                    <p class="card-text"><strong>Ставка:</strong> ${bid.amount}₽</p>
                    <p class="card-text"><strong>Дата:</strong> ${new Date(bid.createdAt).toLocaleString()}</p>
                </div>
            </div>
        `;
        container.appendChild(bidElement);
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
        logoutButton.onclick = () => {
            localStorage.removeItem('userId');
            window.location.href = 'login.html';
        };
    } else {
        profileLink.style.display = 'none';
        logoutButton.style.display = 'none';
        loginLink.style.display = 'block';
        registerLink.style.display = 'block';
    }
}