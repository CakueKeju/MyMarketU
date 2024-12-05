document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const response = await fetch("http://localhost:8080/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
    });

    const result = await response.json();
    document.getElementById("responseMessage").innerText = result.message;
});
const loginForm = document.getElementById('login-form');
const registerLink = document.getElementById('register-link');
const loginLink = document.getElementById('login-link');

loginForm.addEventListener('submit', (event) => {
  event.preventDefault();
  // Add login logic here
  console.log('Login form submitted');
});

registerLink.addEventListener('click', () => {
  window.location.href = 'register.html';
});

loginLink.addEventListener('click', () => {
  window.location.href = 'index.html';
  loginLink.style.opacity = '0';
  loginLink.style.transition = 'opacity 0.5s ease-in-out';
});