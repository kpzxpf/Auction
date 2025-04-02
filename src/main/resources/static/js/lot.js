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

        const imagesDiv = document.getElementById('images');
        if (lot.images && lot.images.length > 0) {
            lot.images.forEach(image => {
                const img = document.createElement('img');
                img.src = image.url;
                img.alt = lot.title;
                img.className = 'img-thumbnail me-2';
                img.style.maxWidth = '150px';
                imagesDiv.appendChild(img);
            });
        } else {
            imagesDiv.innerHTML = '<p class="text-muted">Изображения отсутствуют</p>';
        }

        const endTime = new Date(lot.endTime).getTime();
        startCountdown(endTime);

        if (userId) {
            document.getElementById('bidForm').style.display = 'block';
            setupBidForm(lotId, userId);
        } else {
            document.getElementById('bidForm').outerHTML = '<p class="text-muted">Войдите, чтобы сделать ставку</p>';
        }

        const socket = new SockJS('/ws');
        const stompClient = Stomp.over(socket);
        stompClient.connect({}, () => {
            stompClient.subscribe(`/topic/lots/${lotId}`, (message) => {
                const newPrice = parseFloat(message.body);
                document.getElementById('currentPrice').textContent = newPrice.toFixed(2);
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

function startCountdown(endTime) {
    const timerElement = document.getElementById('timer');
    const interval = setInterval(() => {
        const now = new Date().getTime();
        const distance = endTime - now;

        if (distance <= 0) {
            clearInterval(interval);
            timerElement.textContent = 'Аукцион завершён';
            document.getElementById('bidForm').style.display = 'none';
        } else {
            const days = Math.floor(distance / (1000 * 60 * 60 * 24));
            const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((distance % (1000 * 60)) / 1000);
            timerElement.textContent = `Осталось: ${days}д ${hours}ч ${minutes}м ${seconds}с`;
        }
    }, 1000);
}