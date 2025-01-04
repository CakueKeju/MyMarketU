-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 04, 2025 at 03:05 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mymarketu`
--

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `invoice_number` varchar(255) NOT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  `order_date` datetime(6) NOT NULL,
  `order_evidence` varchar(255) DEFAULT NULL,
  `status` enum('APPROVED','DECLINED','PENDING') NOT NULL,
  `total_price` double NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `penilaian`
--

CREATE TABLE `penilaian` (
  `id` bigint(20) NOT NULL,
  `id_produk` bigint(20) NOT NULL,
  `komentar` varchar(255) NOT NULL,
  `rating` int(11) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `id_pelanggan` bigint(20) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `produk`
--

CREATE TABLE `produk` (
  `id` int(11) NOT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `kategori` varchar(255) DEFAULT NULL,
  `deskripsi` text DEFAULT NULL,
  `harga` double NOT NULL,
  `stok` int(11) DEFAULT NULL,
  `nama_file_gambar` varchar(255) DEFAULT NULL,
  `diskon` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `produk`
--

INSERT INTO `produk` (`id`, `nama`, `kategori`, `deskripsi`, `harga`, `stok`, `nama_file_gambar`, `diskon`) VALUES
(1, 'Mister Potato Snack Crisps Original 35G', 'Snack', 'Keripik kentang original renyah dan gurih.', 10999, 28, 'mister_potato_original.jpg', 0),
(2, 'Chitato Snack Potato Chips Sapi Panggang 68G', 'Snack', 'Keripik kentang rasa sapi panggang dengan bumbu gurih.', 9000, 14, 'chitato_sapi_panggang.jpg', 0),
(3, 'Melon Segar', 'Buah', 'Buah melon segar dengan rasa manis alami.', 36000, 25, 'melon_segar.jpg', 0),
(4, 'Fruit Tea Minuman Teh Blackcurrant 250ml', 'Minuman', 'Minuman teh rasa blackcurrant yang menyegarkan.', 5900, 49, 'fruit_tea_blackcurrant.jpg', 50),
(5, 'Tisu Wajah', 'Kebutuhan Rumah Tangga', 'Tisu wajah lembut dan nyaman untuk kulit.', 13000, 100, 'tisu_wajah.jpg', 0),
(6, 'Bango Kecap Manis Refill 735ml', 'Bumbu Dapur', 'Kecap manis berkualitas dengan rasa khas.', 26700, 37, 'bango_kecap_manis.jpg', 0),
(7, 'Indomilk Kental Manis Putih 545G', 'Bahan Minuman', 'Susu kental manis putih untuk berbagai keperluan masak.', 14400, 35, 'indomilk_kental_manis.jpg', 0),
(8, 'Pisang Cavendish Single 1S', 'Buah', 'Pisang cavendish segar kaya nutrisi.', 3800, 60, 'pisang_cavendish.jpg', 0),
(9, 'Onigiri PREMIUM Salmon Krimi', 'Makanan Siap Saji', 'Nasi kepal onigiri dengan isian salmon dan krim.', 11000, 20, 'onigiri_salmon_krimi.jpg', 0),
(10, 'Kapal Api Kopi Bubuk Special 200G', 'Minuman', 'Kopi bubuk dengan aroma khas dari Kapal Api.', 18800, 50, 'kapal_api_kopi_bubuk.jpg', 0),
(13, 'Meiji Almond', 'Snack', 'Meiji Almond Uma Musume', 200000, 10, '1734352675209_meijialmond.jpg', 0),
(17, 'Scotch Tape', 'Kebutuhan', 'Scotch tape 5 meter ', 10000, 90, '1735740499968_skibididsihdaisd.jpg', 0);

-- --------------------------------------------------------

--
-- Table structure for table `promo`
--

CREATE TABLE `promo` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `code` varchar(255) NOT NULL,
  `discount_percentage` double NOT NULL,
  `maximum_discount` double NOT NULL,
  `minimum_spend` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `promo`
--

INSERT INTO `promo` (`id`, `active`, `code`, `discount_percentage`, `maximum_discount`, `minimum_spend`) VALUES
(1, b'1', 'DISKON10', 10, 20000, 10000);

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

CREATE TABLE `role` (
  `id` bigint(20) NOT NULL,
  `nama` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (`id`, `nama`) VALUES
(1, 'Admin'),
(2, 'Customer');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `foto_profil` varchar(255) NOT NULL,
  `nama_lengkap` varchar(255) NOT NULL,
  `nim` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `is_active` int(11) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `created_at`, `email`, `foto_profil`, `nama_lengkap`, `nim`, `password`, `updated_at`, `role_id`, `is_active`, `phone`) VALUES
(1, '2024-01-01 10:00:00.000000', 'user1@example.com', 'default-profile.jpg', 'Jane Doe', '123456789', '$2a$10$eEh.ULVLkgH0KFl6i6U5MO6hOizOF9iU9fE0aCFbj2UAZwXk/oPy2', '2025-01-01 14:10:26.000000', 1, NULL, NULL),
(2, '2024-12-23 22:55:25.000000', 'azki@gmail.com', 'default.png', 'Azki', '1301223255', '$2a$10$xxKU6gUdtAhBFhhzRtPrduKPNKcoaE3U21LBOC4oQEYPnOIltiMNi', '2024-12-23 22:55:25.000000', 2, NULL, NULL),
(3, '2024-12-24 09:08:52.000000', 'contoh@gmail.com', 'default.png', 'Skibidi', '1301223252', '$2a$10$nuqJ0XzHgvdqm54jYmXpmeutkQFd0hW5T0RAOe0GoR3Y2y3suEEcW', '2024-12-24 09:08:52.000000', 2, NULL, NULL),
(5, '2024-12-24 16:37:24.000000', 'test@gmail.com', 'default.png', 'cobacoba', '1301223253', '$2a$10$GDpNo0uh2YNw7XF6xpLr6.opY29ok5VFgRuPXyVxnMHYWDq19g25O', '2024-12-24 16:37:24.000000', 2, NULL, NULL),
(6, '2025-01-01 12:44:07.000000', 'mualani123@gmail.com', 'default.png', 'mualani', '1301223299', '$2a$10$ebZZ6LV1khK5ZbXTAmPRyeQTOlxHTFUjPTSqDDBJ/MaWqaKklTaj6', '2025-01-01 12:44:07.000000', 2, 1, NULL),
(10, '2025-01-01 19:44:07.000000', 'admin@contoh.com', 'fa747905-aade-4e9e-93d4-5f9d77c823e9_skibididsihdaisd.jpg', 'Supaadmin', '1301223250', '$2a$10$H0FGuSzgTc47cnKy8ULzxux8m4iK7WAbf.3TFIMLHF.T0h0Ixhk.K', '2025-01-02 05:26:19.000000', 1, 1, '08123456789');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK9euhgrj490gy02d8abquh7jvu` (`invoice_number`),
  ADD UNIQUE KEY `UKk8kupdtcdpqd57b6j4yq9uvdj` (`user_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKnhvovp0wdg7iy73rdpeo2nb8d` (`product_id`),
  ADD KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`);

--
-- Indexes for table `penilaian`
--
ALTER TABLE `penilaian`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `produk`
--
ALTER TABLE `produk`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `promo`
--
ALTER TABLE `promo`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKnhtdlm0x1165d20w8c3fpudin` (`code`);

--
-- Indexes for table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  ADD UNIQUE KEY `UKh5k5eqgbwmq6sh7k3nqfr1439` (`nim`),
  ADD KEY `FK4qu1gr772nnf6ve5af002rwya` (`role_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `penilaian`
--
ALTER TABLE `penilaian`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `produk`
--
ALTER TABLE `produk`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `promo`
--
ALTER TABLE `promo`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `role`
--
ALTER TABLE `role`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  ADD CONSTRAINT `FKnhvovp0wdg7iy73rdpeo2nb8d` FOREIGN KEY (`product_id`) REFERENCES `produk` (`id`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `FK4qu1gr772nnf6ve5af002rwya` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
