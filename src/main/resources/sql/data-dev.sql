insert into users (user_id, username) values
('773d20b6-bbf1-4c10-b743-5e7b693ef3ee', 'robert'),
('91b18ecd-1824-402e-961b-a3285fdaa1b8', 'joe');

insert into contacts (contact_id, name, user_id) values
('5c21433c-3c70-4253-a4b2-52b157be4167', 'Greg from accounting', '91b18ecd-1824-402e-961b-a3285fdaa1b8'),
('4fe25947-ecab-489c-a881-e0057124e408', 'Coworker Fred', '91b18ecd-1824-402e-961b-a3285fdaa1b8'),
('35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7', 'Sister Monica', '91b18ecd-1824-402e-961b-a3285fdaa1b8'),
('7f23057f-77bd-4568-ac64-e933abae9a09', 'Best friend Julia', '773d20b6-bbf1-4c10-b743-5e7b693ef3ee'),
('84edd1b9-89a5-4107-a84d-435676c2b8f5', 'Mom', '773d20b6-bbf1-4c10-b743-5e7b693ef3ee'),
('8fb2bd75-9aec-4cc5-b77b-a95f06081388', 'Pizza and burgers', '773d20b6-bbf1-4c10-b743-5e7b693ef3ee'),
('b621650d-4a81-4016-a917-4a8a4992aaef', 'Uncle Jeff', '773d20b6-bbf1-4c10-b743-5e7b693ef3ee');

insert into phone_numbers (phone_number, contact_id, type) values
('+359(26)5948-0427', '5c21433c-3c70-4253-a4b2-52b157be4167', 'home'),
('+52(54)6536-5876', '4fe25947-ecab-489c-a881-e0057124e408', 'home'),
('+81(56)4205-8516', '4fe25947-ecab-489c-a881-e0057124e408', 'mobile'),
('+359(10)4094-9549', '4fe25947-ecab-489c-a881-e0057124e408', 'office'),
('+65(77)4248-0921', '35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7', 'office'),
('+31(47)1688-3562', '7f23057f-77bd-4568-ac64-e933abae9a09', 'home'),
('+39(05)8263-6323', '7f23057f-77bd-4568-ac64-e933abae9a09', 'office'),
('+1(22)2514-4183', '7f23057f-77bd-4568-ac64-e933abae9a09', 'office2'),
('+65(91)6788-9156', '84edd1b9-89a5-4107-a84d-435676c2b8f5', 'home'),
('+81(78)8606-4615', '8fb2bd75-9aec-4cc5-b77b-a95f06081388', 'main'),
('+39(80)9464-0706', 'b621650d-4a81-4016-a917-4a8a4992aaef', 'home'),
('+31(14)1750-4453', 'b621650d-4a81-4016-a917-4a8a4992aaef', 'mobile');

insert into addresses (type, contact_id, street, country, city, state, zipcode) values
('home', '5c21433c-3c70-4253-a4b2-52b157be4167', '343-1199, Tennodai', 'Japan', 'Abiko-shi', 'Chiba', '02169'),
('work', '5c21433c-3c70-4253-a4b2-52b157be4167', '127-1121, Hiyamizu', 'Japan', 'Rankoshi-cho Isoya-gun', 'Hokkaido', '02169'),
('home', '4fe25947-ecab-489c-a881-e0057124e408', '4454 Steve Hunt Road', 'EUA', 'Miami', 'Florida', '33131'),
('home', '35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7', '4529 Jehovah Drive', 'EUA', 'Waynesboro', 'Virginia', '22980'),
('store 1', '8fb2bd75-9aec-4cc5-b77b-a95f06081388', '3267 Mercer Street', 'EUA', 'San Diego', 'California', '92119'),
('store 2', '8fb2bd75-9aec-4cc5-b77b-a95f06081388', '2644 Arron Smith Drive', 'EUA', 'Thelma', 'Kentucky', '41260'),
('store 3', '8fb2bd75-9aec-4cc5-b77b-a95f06081388', '2221 Spruce Drive', 'EUA', 'Core', 'Pennsylvania', '26529'),
('home', '84edd1b9-89a5-4107-a84d-435676c2b8f5', '2259 Sycamore Fork Road', 'EUA', 'Hopkins', 'Minnesota', '55343'),
('home', '7f23057f-77bd-4568-ac64-e933abae9a09', '1116 Mahlon Street', 'EUA', 'Farmington Hills', 'Michigan', '48335'),
('home', 'b621650d-4a81-4016-a917-4a8a4992aaef', '237-1233, Ichihasama Shimmai', 'Japan', 'Kurihara-shi', 'Miyagi', '46231'),
('work', 'b621650d-4a81-4016-a917-4a8a4992aaef', '210-1040, Okada', 'Japan', 'Chikushino-shi', 'Fukuoka', '48335');

insert into emails (type, email, contact_id) values
('main', 'sailor.greg99@hotmail.co.jp', '5c21433c-3c70-4253-a4b2-52b157be4167'),
('main', 'yuki.fred@gmail.com', '4fe25947-ecab-489c-a881-e0057124e408'),
('main', 'usermonica01@outlook.com', '35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7'),
('main', 'rick.julia@zipmail.com', '7f23057f-77bd-4568-ac64-e933abae9a09'),
('second', 'juliarcs@outlook.com', '7f23057f-77bd-4568-ac64-e933abae9a09'),
('third', 'contactforjulia@wolf.com', '7f23057f-77bd-4568-ac64-e933abae9a09'),
('main', 'Sheyla.orton@hoppe.org', '84edd1b9-89a5-4107-a84d-435676c2b8f5'),
('main', 'pizzaandburgers.main@amazon.com', '8fb2bd75-9aec-4cc5-b77b-a95f06081388'),
('second', 'pizzaandburgers.store2@amazon.com', '8fb2bd75-9aec-4cc5-b77b-a95f06081388'),
('main', 'contactforjeff.now@yahoo.com', 'b621650d-4a81-4016-a917-4a8a4992aaef');