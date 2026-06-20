-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1
-- Время создания: Июн 20 2026 г., 20:58
-- Версия сервера: 10.4.32-MariaDB
-- Версия PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `salon`
--

-- --------------------------------------------------------

--
-- Структура таблицы `branches`
--

CREATE TABLE `branches` (
  `id_branch` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `clients`
--

CREATE TABLE `clients` (
  `id_client` int(11) NOT NULL,
  `surname` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `patronymic` varchar(50) DEFAULT NULL,
  `phone` varchar(16) NOT NULL,
  `regular_client` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `haircut_types`
--

CREATE TABLE `haircut_types` (
  `id_haircut_type` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `gender` enum('Мужской','Женский','Унисекс') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `haircut_types`
--

INSERT INTO `haircut_types` (`id_haircut_type`, `name`, `gender`) VALUES
(1, 'Мужская стрижка', 'Мужской'),
(2, 'Женская стрижка', 'Женский'),
(3, 'Детская стрижка', 'Унисекс'),
(4, 'Кроп', 'Мужской');

-- --------------------------------------------------------

--
-- Структура таблицы `prices`
--

CREATE TABLE `prices` (
  `id_price` int(11) NOT NULL,
  `id_haircut_type` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `price` decimal(8,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `roles`
--

CREATE TABLE `roles` (
  `id_role` int(11) NOT NULL,
  `role_name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `roles`
--

INSERT INTO `roles` (`id_role`, `role_name`) VALUES
(1, 'ADMIN'),
(2, 'USER');

-- --------------------------------------------------------

--
-- Структура таблицы `services`
--

CREATE TABLE `services` (
  `id_service` int(11) NOT NULL,
  `id_client` int(11) NOT NULL,
  `id_branch` int(11) NOT NULL,
  `id_price` int(11) NOT NULL,
  `service_date` datetime NOT NULL,
  `total_cost` decimal(8,2) NOT NULL,
  `discount_applied` tinyint(1) NOT NULL DEFAULT 0,
  `client_wishes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Триггеры `services`
--
DELIMITER $$
CREATE TRIGGER `trg_services_after_insert` AFTER INSERT ON `services` FOR EACH ROW BEGIN
    DECLARE service_count INT;

    SELECT COUNT(*)
    INTO service_count
    FROM services
    WHERE id_client = NEW.id_client;

    IF service_count >= 4 THEN
        UPDATE clients
        SET regular_client = TRUE
        WHERE id_client = NEW.id_client;
    END IF;
    
    IF service_count < 4 THEN
        UPDATE clients
        SET regular_client = FALSE
        WHERE id_client = NEW.id_client;
    END IF;

END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_services_before_insert` BEFORE INSERT ON `services` FOR EACH ROW BEGIN
    DECLARE client_regular BOOLEAN;
    DECLARE haircut_price DECIMAL(8,2);
    DECLARE service_count INT;

    SELECT regular_client
    INTO client_regular
    FROM clients
    WHERE id_client = NEW.id_client;

    SELECT price
    INTO haircut_price
    FROM prices
    WHERE id_price = NEW.id_price;

    SELECT COUNT(*)
    INTO service_count
    FROM services
    WHERE id_client = NEW.id_client;

    -- Скидка: постоянный клиент И уже >= 4 услуг
    IF client_regular = TRUE AND service_count >= 4 THEN
        SET NEW.discount_applied = TRUE;
        SET NEW.total_cost = ROUND(haircut_price * 0.97, 2);
    ELSE
        SET NEW.discount_applied = FALSE;
        SET NEW.total_cost = haircut_price;
    END IF;

END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Дублирующая структура для представления `service_history`
-- (См. Ниже фактическое представление)
--
CREATE TABLE `service_history` (
`id_service` int(11)
,`surname` varchar(50)
,`name` varchar(50)
,`haircut` varchar(100)
,`branch` varchar(100)
,`service_date` datetime
,`total_cost` decimal(8,2)
,`discount_applied` tinyint(1)
);

-- --------------------------------------------------------

--
-- Структура таблицы `users`
--

CREATE TABLE `users` (
  `id_user` int(11) NOT NULL,
  `id_role` int(11) NOT NULL,
  `login` varchar(50) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `registration_date` datetime NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура для представления `service_history`
--
DROP TABLE IF EXISTS `service_history`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `service_history`  AS SELECT `s`.`id_service` AS `id_service`, `c`.`surname` AS `surname`, `c`.`name` AS `name`, `ht`.`name` AS `haircut`, `b`.`name` AS `branch`, `s`.`service_date` AS `service_date`, `s`.`total_cost` AS `total_cost`, `s`.`discount_applied` AS `discount_applied` FROM ((((`services` `s` join `clients` `c` on(`s`.`id_client` = `c`.`id_client`)) join `branches` `b` on(`s`.`id_branch` = `b`.`id_branch`)) join `prices` `p` on(`s`.`id_price` = `p`.`id_price`)) join `haircut_types` `ht` on(`p`.`id_haircut_type` = `ht`.`id_haircut_type`)) ;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `branches`
--
ALTER TABLE `branches`
  ADD PRIMARY KEY (`id_branch`);

--
-- Индексы таблицы `clients`
--
ALTER TABLE `clients`
  ADD PRIMARY KEY (`id_client`),
  ADD UNIQUE KEY `phone` (`phone`);

--
-- Индексы таблицы `haircut_types`
--
ALTER TABLE `haircut_types`
  ADD PRIMARY KEY (`id_haircut_type`);

--
-- Индексы таблицы `prices`
--
ALTER TABLE `prices`
  ADD PRIMARY KEY (`id_price`),
  ADD UNIQUE KEY `id_haircut_type` (`id_haircut_type`,`start_date`);

--
-- Индексы таблицы `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id_role`),
  ADD UNIQUE KEY `role_name` (`role_name`);

--
-- Индексы таблицы `services`
--
ALTER TABLE `services`
  ADD PRIMARY KEY (`id_service`),
  ADD KEY `services_ibfk_1` (`id_client`),
  ADD KEY `services_ibfk_2` (`id_branch`),
  ADD KEY `services_ibfk_3` (`id_price`),
  ADD KEY `idx_service_date` (`service_date`);

--
-- Индексы таблицы `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `login` (`login`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `users_ibfk_1` (`id_role`),
  ADD KEY `idx_registration_date` (`registration_date`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `branches`
--
ALTER TABLE `branches`
  MODIFY `id_branch` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT для таблицы `clients`
--
ALTER TABLE `clients`
  MODIFY `id_client` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT для таблицы `haircut_types`
--
ALTER TABLE `haircut_types`
  MODIFY `id_haircut_type` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT для таблицы `prices`
--
ALTER TABLE `prices`
  MODIFY `id_price` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT для таблицы `roles`
--
ALTER TABLE `roles`
  MODIFY `id_role` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT для таблицы `services`
--
ALTER TABLE `services`
  MODIFY `id_service` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT для таблицы `users`
--
ALTER TABLE `users`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `prices`
--
ALTER TABLE `prices`
  ADD CONSTRAINT `prices_ibfk_1` FOREIGN KEY (`id_haircut_type`) REFERENCES `haircut_types` (`id_haircut_type`);

--
-- Ограничения внешнего ключа таблицы `services`
--
ALTER TABLE `services`
  ADD CONSTRAINT `services_ibfk_1` FOREIGN KEY (`id_client`) REFERENCES `clients` (`id_client`) ON UPDATE CASCADE,
  ADD CONSTRAINT `services_ibfk_2` FOREIGN KEY (`id_branch`) REFERENCES `branches` (`id_branch`) ON UPDATE CASCADE,
  ADD CONSTRAINT `services_ibfk_3` FOREIGN KEY (`id_price`) REFERENCES `prices` (`id_price`) ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`id_role`) REFERENCES `roles` (`id_role`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
