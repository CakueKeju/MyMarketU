$(document).ready(function () {
    $('#apply-promo-button').click(applyPromo);
    function loadCart() {
        const cart = JSON.parse(localStorage.getItem("cart")) || [];
        renderCart(cart);
    }

    function renderCart(cart) {
        const cartContainer = $('#cart-items');
        cartContainer.empty();

        if (cart.length === 0) {
            $('#empty-cart-message').removeClass('d-none');
            $('#checkout-button').prop('disabled', true);
            return;
        }

        $('#empty-cart-message').addClass('d-none');
        $('#checkout-button').prop('disabled', false);

        let subtotal = 0;

        cart.forEach(item => {
            subtotal += item.productPrice * item.quantity;
            const productCard = `
                <div class="col-md-4 mb-4">
                    <div class="card h-100 shadow-sm">
                        <img src="/images/${item.imageName}" class="card-img-top product-img" alt="${item.productName}">
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title">${item.productName}</h5>
                            <p class="text-muted">Rp ${item.productPrice}</p>
                            <p class="text-muted small mb-3">Jumlah: ${item.quantity}</p>
                            <button class="btn btn-danger btn-sm w-100 remove-from-cart" data-id="${item.productId}">Hapus</button>
                        </div>
                    </div>
                </div>
            `;
            cartContainer.append(productCard);
        });

        $('#subtotal').text(`Rp ${subtotal}`);
        $('#total-price').text(`Rp ${subtotal}`);
    }

    function addToCart(productId, productName, productPrice, quantity, imageName) {
        let cart = JSON.parse(localStorage.getItem("cart")) || [];
        const existingItem = cart.find(item => item.productId === productId);

        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            cart.push({ productId, productName, productPrice, quantity, imageName });
        }

        localStorage.setItem("cart", JSON.stringify(cart));
        toastr.success(`Produk "${productName}" berhasil ditambahkan ke keranjang!`);
        loadCart();
    }

    function removeFromCart(productId) {
        let cart = JSON.parse(localStorage.getItem("cart")) || [];
        cart = cart.filter(item => item.productId !== productId);

        localStorage.setItem("cart", JSON.stringify(cart));
        toastr.success("Produk berhasil dihapus dari keranjang.");
        loadCart();
    }

    function clearCart() {
        localStorage.removeItem("cart");
        toastr.info("Keranjang berhasil dikosongkan.");
        loadCart();
    }

    
    $(document).on('click', '.add-to-cart-btn', function () {
        const form = $(this).closest('form');
        const productId = parseInt(form.find('input[name="productId"]').val());
        const productName = form.find('input[name="productName"]').val();
        const productPrice = parseFloat(form.find('input[name="productPrice"]').val());
        const quantity = parseInt(form.find('input[name="quantity"]').val());
        const imageName = form.find('input[name="imageName"]').val();

        addToCart(productId, productName, productPrice, quantity, imageName);
    });

    $(document).on('click', '.remove-from-cart', function () {
        const productId = $(this).data('id');
        removeFromCart(productId);
    });

    $('#checkout-button').click(checkout);
    $('#clear-cart-button').click(clearCart);

    loadCart();
    
    function applyPromo() {
    const promoCode = document.getElementById('promo-code').value;
    const subtotal = parseFloat(document.getElementById('subtotal').textContent.replace('Rp ', '').replace(',', ''));

    fetch(`/customer/cart/apply-promo?subtotal=${subtotal}&promoCode=${promoCode}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Kode promo tidak valid");
            }
        })
        .then(discount => {
            document.getElementById('discount').textContent = `- Rp ${discount}`;
            document.getElementById('total-price').textContent = `Rp ${subtotal - discount}`;
            document.getElementById('promo-error').textContent = ""; // Reset error
        })
        .catch(error => {
            document.getElementById('promo-error').textContent = error.message;
        });
    }

});

function checkout() {
        const cart = JSON.parse(localStorage.getItem("cart")) || [];
        const promoCode = document.getElementById("promo-code").value;

        if (cart.length === 0) {
            alert("Keranjang Anda kosong!");
            return;
        }

        // Siapkan payload untuk dikirim
        const payload = {
            userId: 1, // Ganti dengan userId aktual jika tersedia
            promoCode: promoCode,
            cartItems: cart
        };

        // Kirim request POST ke backend
        fetch('/customer/cart/checkout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error("Checkout gagal!");
            }
        })
        .then(data => {
            console.log(data);
            window.location.href = data; // Redirect ke halaman checkout
        })
        .catch(error => {
            console.error(error);
            alert("Terjadi kesalahan saat checkout.");
        });
}