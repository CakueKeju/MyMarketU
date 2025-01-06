document.addEventListener("DOMContentLoaded", function () {
    console.log("homepage.js loaded successfully");

    // Fungsi untuk menambahkan produk ke keranjang di local storage
    function addToCart(productId, productName, productPrice, quantity, imageName) {
        let cart = JSON.parse(localStorage.getItem("cart")) || [];
        const existingItem = cart.find(item => item.productId === productId);

        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            cart.push({ productId, productName, productPrice, quantity, imageName });
        }

        console.log("Updated Cart:", cart); // Debugging isi keranjang

        localStorage.setItem("cart", JSON.stringify(cart));
        toastr.success(`Produk "${productName}" berhasil ditambah ke keranjang!`);
    }

    // Event listener untuk tombol "Tambahkan"
    document.querySelectorAll('.add-to-cart-btn').forEach(button => {
        button.addEventListener('click', function () {
            const form = this.closest('form');
            if (!form) {
                console.error("Form tidak ditemukan untuk tombol ini.");
                return;
            }

            const productId = parseInt(form.querySelector('input[name="productId"]').value);
            const productName = form.querySelector('input[name="productName"]').value;
            const productPrice = parseFloat(form.querySelector('input[name="productPrice"]').value);
            const quantity = parseInt(form.querySelector('input[name="quantity"]').value);
            const imageName = form.querySelector('input[name="imageName"]').value;

            console.log("Menambahkan ke keranjang:", { productId, productName, productPrice, quantity, imageName });

            addToCart(productId, productName, productPrice, quantity, imageName);
        });
    });
});
