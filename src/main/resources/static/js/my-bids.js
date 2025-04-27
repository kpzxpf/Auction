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
    const loadingElement = document.getElementById('loading');
    loadingElement.style.display = 'block';

    try {
        const bidsResponse = await fetch(`http://localhost:8080/bids/user/${userId}`, {
            headers: { 'X-User-Id': userId }
        });
        if (!bidsResponse.ok) throw new Error('Ошибка загрузки ставок');
        const bids = await bidsResponse.json();

        for (const bid of bids) {
            const lotResponse = await fetch(`http://localhost:8080/lots/${bid.lotId}`, {
                headers: { 'X-User-Id': userId }
            });
            if (!lotResponse.ok) throw new Error(`Ошибка загрузки лота ${bid.lotId}`);
            const lot = await lotResponse.json();
            bid.lotTitle = lot.title;
            bid.endTime = lot.endTime;
            bid.currentPrice = lot.currentPrice;
        }

        displayUserBids(bids);
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка при загрузке ставок');
        document.getElementById('bids-list').innerHTML = '<p class="text-center">Не удалось загрузить ставки</p>';
    } finally {
        loadingElement.style.display = 'none';
    }
}

function displayUserBids(bids) {
    const container = document.getElementById('bids-list');
    container.innerHTML = '';
    if (bids.length === 0) {
        container.innerHTML = '<p class="text-center text-muted">Вы еще не сделали ставок.</p>';
        return;
    }

    bids.forEach(bid => {
        const bidElement = document.createElement('div');
        bidElement.className = 'col-md-4 mb-4';

        // Определяем статус ставки
        const now = new Date();
        const endTime = new Date(bid.endTime);
        const isEnded = endTime < now;
        const isWinning = bid.currentPrice === bid.amount.toString() && isEnded;
        let statusClass = 'status-pending';
        let statusText = 'В процессе';
        if (isEnded) {
            statusClass = isWinning ? 'status-winning' : 'status-lost';
            statusText = isWinning ? 'Выигрышная' : 'Проигранная';
        }

        bidElement.innerHTML = `
            <div class="bid-card">
                <div class="card-body">
                    <h5 class="card-title">${bid.lotTitle || 'Без названия'}</h5>
                    <p class="card-text"><strong>Ставка:</strong> ${Number(bid.amount).toFixed(2)}₽</p>
                    <p class="card-text"><strong>Дата:</strong> ${new Date(bid.bidTime).toLocaleString('ru-RU')}</p>
                    <p class="card-text"><strong>Окончание:</strong> ${new Date(bid.endTime).toLocaleString('ru-RU')}</p>
                    <span class="status ${statusClass}">${statusText}</span>
                    <button class="btn btn-primary btn-lot" onclick="window.location.href='lot.html?id=${bid.lotId}'">
                        Перейти к лоту
                    </button>
                </div>
            </div>
        `;
        container.appendChild(bidElement);
    });
}

function updateNavigation() {
    const userId = localStorage.getItem('userId');
    const profileLink = document.getElementById('profileLink');
    const loginLink = document.getElementById('loginLink');
    const registerLink = document.getElementById('registerLink');

    if (userId) {
        profileLink.style.display = 'block';
        loginLink.style.display = 'none';
        registerLink.style.display = 'none';
    } else {
        profileLink.style.display = 'none';
        loginLink.style.display = 'block';
        registerLink.style.display = 'block';
    }
}