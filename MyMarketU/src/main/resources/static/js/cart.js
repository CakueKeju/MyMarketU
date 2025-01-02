/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

<script>
    document.getElementById('apply-promo-btn').addEventListener('click', function () {
        const promoCode = document.getElementById('promo-code').value;

        fetch('/customer/cart/apply-promo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `code=${promoCode}`
        })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                return response.text().then(text => { throw new Error(text); });
            }
        })
        .then(message => {
            document.getElementById('promo-message').textContent = message;
            location.reload(); // Reload halaman untuk memperbarui total
        })
        .catch(error => {
            document.getElementById('promo-message').textContent = error.message;
            document.getElementById('promo-message').classList.remove('text-success');
            document.getElementById('promo-message').classList.add('text-danger');
        });
    });
</script>

