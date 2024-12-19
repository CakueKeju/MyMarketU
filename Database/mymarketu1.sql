-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 19, 2024 at 09:11 AM
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
-- Table structure for table `produk`
--

CREATE TABLE `produk` (
  `id` int(11) NOT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `kategori` varchar(255) DEFAULT NULL,
  `deskripsi` text DEFAULT NULL,
  `harga` double NOT NULL,
  `stok` int(11) DEFAULT NULL,
  `nama_file_gambar` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `produk`
--

INSERT INTO `produk` (`id`, `nama`, `kategori`, `deskripsi`, `harga`, `stok`, `nama_file_gambar`) VALUES
(1, 'Mister Potato Snack Crisps Original 35G', 'Snack', 'Keripik kentang original renyah dan gurih.', 10999, 30, 'mister_potato_original.jpg'),
(2, 'Chitato Snack Potato Chips Sapi Panggang 68G', 'Snack', 'Keripik kentang rasa sapi panggang dengan bumbu gurih.', 9000, 19, 'chitato_sapi_panggang.jpg'),
(3, 'Melon Segar', 'Buah', 'Buah melon segar dengan rasa manis alami.', 36000, 25, 'melon_segar.jpg'),
(4, 'Fruit Tea Minuman Teh Blackcurrant 250ml', 'Minuman', 'Minuman teh rasa blackcurrant yang menyegarkan.', 5900, 50, 'fruit_tea_blackcurrant.jpg'),
(5, 'Tisu Wajah', 'Kebutuhan Rumah Tangga', 'Tisu wajah lembut dan nyaman untuk kulit.', 13000, 100, 'tisu_wajah.jpg'),
(6, 'Bango Kecap Manis Refill 735ml', 'Bumbu Dapur', 'Kecap manis berkualitas dengan rasa khas.', 26700, 40, 'bango_kecap_manis.jpg'),
(7, 'Indomilk Kental Manis Putih 545G', 'Bahan Minuman', 'Susu kental manis putih untuk berbagai keperluan masak.', 14400, 35, 'indomilk_kental_manis.jpg'),
(8, 'Pisang Cavendish Single 1S', 'Buah', 'Pisang cavendish segar kaya nutrisi.', 3800, 60, 'pisang_cavendish.jpg'),
(9, 'Onigiri PREMIUM Salmon Krimi', 'Makanan Siap Saji', 'Nasi kepal onigiri dengan isian salmon dan krim.', 11000, 20, 'onigiri_salmon_krimi.jpg'),
(10, 'Kapal Api Kopi Bubuk Special 200G', 'Minuman', 'Kopi bubuk dengan aroma khas dari Kapal Api.', 18800, 50, 'kapal_api_kopi_bubuk.jpg'),
(13, 'Meiji Almond', 'Snack', 'Meiji Almond Uma Musume', 200000, 10, '1734352675209_meijialmond.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `nim` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `email`, `nim`, `password`, `username`) VALUES
(1, 'mirsyadtriananda@gmail.com', '1301223259', 'akuanaktelkom123', 'cakue');

-- --------------------------------------------------------

--
-- Table structure for table `user_roles`
--

CREATE TABLE `user_roles` (
  `user_id` bigint(20) NOT NULL,
  `roles` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_roles`
--

INSERT INTO `user_roles` (`user_id`, `roles`) VALUES
(1, 'admin');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `produk`
--
ALTER TABLE `produk`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  ADD UNIQUE KEY `UK8hnq1ipaquo91ep07lvtblr3g` (`nim`),
  ADD UNIQUE KEY `UKsb8bbouer5wak8vyiiy4pf2bx` (`username`);

--
-- Indexes for table `user_roles`
--
ALTER TABLE `user_roles`
  ADD KEY `FK55itppkw3i07do3h7qoclqd4k` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `produk`
--
ALTER TABLE `produk`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_roles`
--
ALTER TABLE `user_roles`
  ADD CONSTRAINT `FK55itppkw3i07do3h7qoclqd4k` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
