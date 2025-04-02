document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (!response.ok) throw new Error('Неверные учетные данные');
            const user = await response.json();
            localStorage.setItem('userId', user.id);
            window.location.href = 'index.html';
        } catch (error) {
            document.getElementById('login-message').innerHTML =
                `<div class="alert alert-danger">${error.message}</div>`;
        }
    });
});