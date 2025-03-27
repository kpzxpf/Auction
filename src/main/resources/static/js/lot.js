document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const lotId = params.get("id");
    if (lotId) {
        loadAuctionDetails(lotId);
        setupBidForm(lotId);
    }
});

async function loadAuctionDetails(lotId) {
    try {
        const response = await fetch(`http://localhost:8080/lots/${lotId}`);
        if (!response.ok) throw new Error("Лот не найден");

        const lot = await response.json();
        displayAuctionDetails(lot);

    } catch (error) {
        showError(error.message);
    }
}

function displayAuctionDetails(lot) {
    const auctionDetail = document.getElementById("lot");
    auctionDetail.innerHTML = `
        <div class="card">
            <img src="${lot.imageUrl || 'images/banner.jpg'}" 
                 class="card-img-top" 
                 alt="${lot.title}"
                 style="max-height: 500px; object-fit: cover">
            <div class="card-body">
                <h1 class="card-title mb-4">${lot.title}</h1>
                <div class="mb-4">
                    <h4><i class="fas fa-info-circle"></i> Описание</h4>
                    <p class="card-text">${lot.description || 'Нет описания'}</p>
                </div>
                
                <div class="row">
                    <div class="col-md-6">
                        <h4><i class="fas fa-coins"></i> Текущая цена</h4>
                        <p class="text-success fs-3">${lot.currentPrice}₽</p>
                    </div>
                    <div class="col-md-6">
                        <h4><i class="fas fa-clock"></i> Дата окончания</h4>
                        <p class="text-danger fs-5">${new Date(lot.endTime).toLocaleString()}</p>
                    </div>
                </div>
                
                ${lot.status === 'ACTIVE' ? `
                <div class="alert alert-info mt-4">
                    <i class="fas fa-hourglass-start"></i> Аукцион активен
                </div>` : `
                <div class="alert alert-danger mt-4">
                    <i class="fas fa-ban"></i> Аукцион завершен
                </div>`}
            </div>
        </div>
    `;
}

function setupBidForm(lotId) {
    document.getElementById("bid-form").addEventListener("submit", async (event) => {
        event.preventDefault();
        const token = localStorage.getItem("jwtToken");
        if (!token) {
            window.location.href = "login.html";
            return;
        }

        const bidAmount = document.getElementById("bid-amount").value;
        try {
            const response = await fetch(`http://localhost:8080/bid`, { // Обновлено на /bid
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ lotId, amount: bidAmount })
            });
            if (!response.ok) throw new Error("Ошибка подачи ставки");
            alert("Ставка успешно подана!");
            loadAuctionDetails(lotId);
        } catch (error) {
            console.error(error);
            alert("Ошибка при подаче ставки");
        }
    });
}

function showError(message) {
    const auctionDetail = document.getElementById("lot");
    auctionDetail.innerHTML = `
        <div class="alert alert-danger">${message}</div>
    `;
}