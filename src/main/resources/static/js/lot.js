document.addEventListener('DOMContentLoaded', async () => {
    const lotId = new URLSearchParams(window.location.search).get('id');
    if (!lotId) {
        alert('ID лота не указан');
        return;
    }


    const userId = localStorage.getItem('userId');
    let userBalance = 0;

    try {
        // Загружаем данные лота
        const lotResponse = await fetch(`http://localhost:8080/lots/${lotId}`, {
            headers: userId ? { 'X-User-Id': userId } : {}
        });
        if (!lotResponse.ok) throw new Error('Лот не найден');
        const lot = await lotResponse.json();

        // Загружаем данные пользователя
        if (userId) {
            const userResp = await fetch(`http://localhost:8080/user/${userId}`, {
                headers: { 'X-User-Id': userId }
            });
            if (userResp.ok) {
                const user = await userResp.json();
                userBalance = user.balance || 0;
                document.getElementById('userBalance').textContent = userBalance.toFixed(2);
            }
        }

        // Рендерим данные лота
        document.getElementById('title').textContent = lot.title;
        document.getElementById('description').textContent = lot.description || 'Описание отсутствует';
        document.getElementById('currentPrice').textContent = lot.currentPrice.toFixed(2);

        // Рендерим карусель изображений
        const carouselInner = document.querySelector('.carousel-inner');
        if (lot.imageUrls?.length > 0) {
            lot.imageUrls.forEach((url, i) => {
                const item = document.createElement('div');
                item.className = `carousel-item ${i === 0 ? 'active' : ''}`;
                item.innerHTML = `<img src="${url}" class="d-block w-100" alt="${lot.title}">`;
                carouselInner.appendChild(item);
            });
        } else {
            document.getElementById('images').innerHTML = '<p class="text-muted">Изображения отсутствуют</p>';
        }

        // Запускаем таймер и историю ставок
        const endTime = new Date(lot.endTime).getTime();
        startCountdown(endTime, Date.now(), lotId);
        await loadBidHistory(lotId);

        // Показываем форму для ставок или сообщение о необходимости входа
        if (userId) {
            document.getElementById('bidForm').style.display = 'block';
            setupBidForm(lotId, userId);
        } else {
            document.getElementById('bidForm').outerHTML = '<p class="text-muted">Войдите, чтобы сделать ставку</p>';
        }

        updateNavigation();

        // WebSocket для обновлений цены и победителя
        const socket = new SockJS('/ws');
        const stompClient = Stomp.over(socket);
        stompClient.connect({}, () => {
            stompClient.subscribe(`/topic/lots/${lotId}`, message => {
                const newPrice = parseFloat(message.body);
                document.getElementById('currentPrice').textContent = newPrice.toFixed(2);
                loadBidHistory(lotId);
            });
            stompClient.subscribe(`/topic/lots/${lotId}/winner`, message => {
                const winner = JSON.parse(message.body);
                document.getElementById('winnerContainer').innerHTML =
                    `<h5>Победитель аукциона: ${winner.userName} с ставкой ${winner.amount.toFixed(2)}₽</h5>`;
            });
        });
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Не удалось загрузить лот');
    }
});

function setupBidForm(lotId, userId) {
    document.getElementById('bidForm').addEventListener('submit', async e => {
        e.preventDefault();
        const bidAmount = parseFloat(document.getElementById('bidAmount').value);
        if (!bidAmount || bidAmount <= 0) {
            alert('Введите корректную сумму ставки');
            return;
        }
        const currentBalance = parseFloat(document.getElementById('userBalance').textContent);
        if (bidAmount > currentBalance) {
            alert('Сумма ставки не может превышать баланс');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/bids', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-User-Id': userId
                },
                body: JSON.stringify({ lotId, userId, amount: bidAmount })
            });
            if (!response.ok) throw new Error('Ошибка при размещении ставки');
            document.getElementById('bidAmount').value = '';
            const newBalance = currentBalance - bidAmount;
            document.getElementById('userBalance').textContent = newBalance.toFixed(2);
        } catch (error) {
            console.error('Ошибка:', error);
            alert('Не удалось сделать ставку');
        }
    });
}

function startCountdown(endTime, startTime, lotId) {
    const timerProgress = document.getElementById('timerProgress');
    const timerText = document.getElementById('timerText');
    const totalDuration = endTime - startTime;
    const circumference = 2 * Math.PI * 55;

    const interval = setInterval(() => {
        const now = Date.now();
        const distance = endTime - now;

        if (distance <= 0) {
            clearInterval(interval);
            if (timerProgress) {
                timerProgress.style.strokeDashoffset = circumference;
                timerProgress.style.stroke = '#dc3545';
            }
            timerText.textContent = '0:00:00:00';
            timerText.classList.add('timer-ended');

            document.getElementById('lotFinishedMessage').style.display = 'block';
            const bidForm = document.getElementById('bidForm');
            if (bidForm) bidForm.style.display = 'none';

            fetch(`http://localhost:8080/transaction/${lotId}/finish`, { method: 'PUT' })
                .then(r => { if (!r.ok) console.warn('Ошибка при завершении лота на сервере'); });
        } else {
            const days = Math.floor(distance / (1000 * 60 * 60 * 24));
            const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((distance % (1000 * 60)) / 1000);

            timerText.textContent = `${days}:${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

            if (timerProgress) {
                const progress = distance / totalDuration;
                const offset = circumference * (1 - progress);
                timerProgress.style.strokeDashoffset = offset;

                if (progress < 0.25) timerProgress.style.stroke = '#dc3545';
                else if (progress < 0.5) timerProgress.style.stroke = '#ffc107';
                else timerProgress.style.stroke = '#28a745';
            }
        }
    }, 1000);
}

async function loadBidHistory(lotId) {
    const userId = localStorage.getItem('userId');
    const headers = userId ? { 'X-User-Id': userId } : {};

    try {
        const url = `http://localhost:8080/bids/lot/${encodeURIComponent(lotId)}`;
        console.log('Loading bid history from', url);
        const response = await fetch(url, { headers });
        if (!response.ok) {
            const errText = await response.text();
            console.error(`Error fetching bid history (${response.status}):`, errText);
            return;
        }
        let bidHistory = await response.json();
        bidHistory.sort((a, b) => b.amount - a.amount);

        const bidTableBody = document.getElementById('bidTableBody');
        bidTableBody.innerHTML = '';

        bidHistory.forEach(bid => {
            const date = new Date(bid.timestamp || bid.bidTime);
            const formatted = isNaN(date) ? '—' : date.toLocaleString();
            const row = document.createElement('tr');
            row.innerHTML = `<td>${bid.amount.toFixed(2)}</td><td>${formatted}</td>`;
            bidTableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Ошибка в loadBidHistory():', error);
    }
}

function updateNavigation() {
    const userId = localStorage.getItem('userId');
    document.getElementById('profileLink').style.display = userId ? 'block' : 'none';
    document.getElementById('loginLink').style.display = userId ? 'none' : 'inline-block';
    document.getElementById('registerLink').style.display = userId ? 'none' : 'inline-block';
}