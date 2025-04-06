document.addEventListener('DOMContentLoaded', async () => {
    const lotId = new URLSearchParams(window.location.search).get('id');
    if (!lotId) {
        alert('ID лота не указан');
        return;
    }

    const userId = localStorage.getItem('userId');

    try {
        const response = await fetch(`http://localhost:8080/lots/${lotId}`, {
            headers: userId ? { 'X-User-Id': userId } : {}
        });
        if (!response.ok) throw new Error('Лот не найден');
        const lot = await response.json();

        document.getElementById('title').textContent = lot.title;
        document.getElementById('description').textContent = lot.description || 'Описание отсутствует';
        document.getElementById('currentPrice').textContent = lot.currentPrice.toFixed(2);

        const carouselInner = document.querySelector('.carousel-inner');
        if (lot.imageUrls && lot.imageUrls.length > 0) {
            lot.imageUrls.forEach((url, index) => {
                const item = document.createElement('div');
                item.className = `carousel-item ${index === 0 ? 'active' : ''}`;
                item.innerHTML = `<img src="${url}" class="d-block w-100" alt="${lot.title}">`;
                carouselInner.appendChild(item);
            });
        } else {
            document.getElementById('images').innerHTML = '<p class="text-muted">Изображения отсутствуют</p>';
        }

        const endTime = new Date(lot.endTime).getTime();
        const startTime = new Date().getTime();
        startCountdown(endTime, startTime);

        await loadBidHistory(lotId);

        if (userId) {
            document.getElementById('bidForm').style.display = 'block';
            setupBidForm(lotId, userId);
        } else {
            document.getElementById('bidForm').outerHTML = '<p class="text-muted">Войдите, чтобы сделать ставку</p>';
        }

        updateNavigation();

        const socket = new SockJS('/ws');
        const stompClient = Stomp.over(socket);
        stompClient.connect({}, () => {
            stompClient.subscribe(`/topic/lots/${lotId}`, (message) => {
                const newPrice = parseFloat(message.body);
                document.getElementById('currentPrice').textContent = newPrice.toFixed(2);
                loadBidHistory(lotId);
            });
        });
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Не удалось загрузить лот');
    }
});

function setupBidForm(lotId, userId) {
    document.getElementById('bidForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const bidAmount = parseFloat(document.getElementById('bidAmount').value);
        if (!bidAmount || bidAmount <= 0) {
            alert('Введите корректную сумму ставки');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/bids', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-User-Id': userId
                },
                body: JSON.stringify({ lotId, amount: bidAmount })
            });

            if (!response.ok) throw new Error('Ошибка при размещении ставки');
            document.getElementById('bidAmount').value = '';
        } catch (error) {
            console.error('Ошибка:', error);
            alert('Не удалось сделать ставку');
        }
    });
}

function startCountdown(endTime, startTime) {
    const timerProgress = document.getElementById('timerProgress');
    const timerText = document.getElementById('timerText');
    const totalDuration = endTime - startTime;
    const circumference = 2 * Math.PI * 55;

    const interval = setInterval(() => {
        const now = new Date().getTime();
        const distance = endTime - now;

        if (distance <= 0) {
            clearInterval(interval);
            timerProgress.style.strokeDashoffset = circumference;
            timerProgress.style.stroke = '#dc3545';
            timerText.textContent = '0:00:00:00';
            timerText.classList.add('timer-ended');
            document.getElementById('bidForm')?.style.display === 'block' && (document.getElementById('bidForm').style.display = 'none');
        } else {
            const days = Math.floor(distance / (1000 * 60 * 60 * 24));
            const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((distance % (1000 * 60)) / 1000);
            timerText.textContent = `${days}:${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

            const progress = distance / totalDuration;
            const offset = circumference * (1 - progress);
            timerProgress.style.strokeDashoffset = offset;

            if (progress < 0.25) {
                timerProgress.style.stroke = '#ffc107';
            }
        }
    }, 1000);
}

async function loadBidHistory(lotId) {
    try {
        const response = await fetch(`http://localhost:8080/bids/lot/${lotId}`);
        if (!response.ok) throw new Error('Не удалось загрузить историю ставок');
        const bids = await response.json();

        const bidTableBody = document.getElementById('bidTableBody');
        bidTableBody.innerHTML = '';

        if (bids.length === 0) {
            bidTableBody.innerHTML = '<tr><td colspan="2" class="text-muted">Ставок пока нет</td></tr>';
            return;
        }

        // Сортировка ставок по убыванию bidTime (от новой к старой)
        bids.sort((a, b) => new Date(b.bidTime) - new Date(a.bidTime));

        bids.forEach(bid => {
            const row = document.createElement('tr');
            const amountCell = document.createElement('td');
            const timeCell = document.createElement('td');
            amountCell.textContent = bid.amount.toFixed(2);
            timeCell.textContent = new Date(bid.bidTime).toLocaleString('ru-RU');
            row.appendChild(amountCell);
            row.appendChild(timeCell);
            bidTableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Ошибка загрузки истории ставок:', error);
        document.getElementById('bidTableBody').innerHTML = '<tr><td colspan="2" class="text-muted">Ошибка загрузки истории</td></tr>';
    }
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
            window.location.reload();
        };
    } else {
        profileLink.style.display = 'none';
        logoutButton.style.display = 'none';
        loginLink.style.display = 'block';
        registerLink.style.display = 'block';
    }
}