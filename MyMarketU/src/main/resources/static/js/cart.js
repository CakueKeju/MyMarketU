$(document).ready(function () {
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

        $('#subtotal').text(`Rp ${subtotal.toLocaleString()}`);
        $('#total-price').text(`Rp ${subtotal.toLocaleString()}`);
    }

    function applyPromoCode() {
         const promoCode = document.getElementById('promo-code').value;

         // Hitung subtotal dari local storage
         const cart = JSON.parse(localStorage.getItem("cart")) || [];
         const subtotal = cart.reduce((sum, item) => sum + (item.productPrice * item.quantity), 0);

         console.log("Calculated Subtotal:", subtotal); // Debugging subtotal

         fetch(`/customer/cart/apply-promo?promoCode=${promoCode}&subtotal=${subtotal}`)
             .then(response => {
                 if (!response.ok) {
                     return response.text().then(text => { throw new Error(text); });
                 }
                 return response.json();
             })
             .then(discount => {
                 document.getElementById('discount').textContent = `- Rp ${discount.toLocaleString()}`;
                 document.getElementById('total-price').textContent = `Rp ${(subtotal - discount).toLocaleString()}`;
                 toastr.success("Promo berhasil diterapkan!", "Sukses");
             })
             .catch(error => {
                 console.error("Error while applying promo:", error); // Debugging error
                 document.getElementById('promo-error').textContent = error.message; w
                 toastr.error(error.message || "Terjadi kesalahan", "Gagal");
             });
     }

    document.getElementById('apply-promo-button').addEventListener('click', applyPromoCode);

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

    $(document).on('click', '.remove-from-cart', function () {
        const productId = $(this).data('id');
        removeFromCart(productId);
    });

    $('#checkout-button').click(function () {
        const userId = 2;
        const promoCode = document.getElementById('promo-code').value || null;
        const cart = JSON.parse(localStorage.getItem("cart")) || [];

        if (cart.length === 0) {
            toastr.error("Keranjang belanja kosong. Tambahkan produk terlebih dahulu!");
            return;
        }

        fetch(`/customer/orders/checkout?userId=${userId}&promoCode=${promoCode}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(cart)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            return response.json();
        })
        .then(orderId => {
            console.log("Order ID:", orderId);
            localStorage.removeItem("cart");
            toastr.success("Checkout berhasil! Mengarahkan ke halaman checkout...");
            window.location.href = `/customer/orders/checkout/${orderId}`;
        })
        .catch(error => {
            console.error("Error during checkout:", error);
            toastr.error(error.message || "Terjadi kesalahan saat proses checkout.", "Gagal");
        });
    });


    $('#clear-cart-button').click(clearCart);

    loadCart();
});
