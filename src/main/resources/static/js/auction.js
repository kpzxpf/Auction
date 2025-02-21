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
        if (!response.ok) throw new Error("Ошибка загрузки лота");

        const lot = await response.json();
        displayAuctionDetails(lot);
    } catch (error) {
        console.error(error);
    }
}

function displayAuctionDetails(lot) {
    const auctionDetail = document.getElementById("auction-detail");
    auctionDetail.innerHTML = `
        <div class="card">
            <img src="${lot.imageUrl || 'images/default.jpg'}" class="card-img-top" alt="${lot.title}">
            <div class="card-body">
                <h2 class="card-title">${lot.title}</h2>
                <p class="card-text"><strong>Описание:</strong> ${lot.description}</p>
                <p class="card-text"><strong>Текущая цена:</strong> ${lot.currentPrice}₽</p>
                <p class="card-text"><strong>Дата окончания:</strong> ${new Date(lot.endTime).toLocaleString()}</p>
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
            const response = await fetch(`http://localhost:8080/bids`, {
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
        }
    });
}
