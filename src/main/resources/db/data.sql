-- 유저 
insert into user_tb(username, password, fullname, created_at)
values('길동', '1234', '고', now());

insert into user_tb(username, password, fullname, created_at)
values('둘리', '1234', '애기공룡', now());

insert into user_tb(username, password, fullname, created_at)
values('마이', '1234', '콜', now());

-- 기본 계좌 등록 
insert into account_tb
		(number, password, balance, user_id, created_at)
values('1111', '1234', 1300, 1, now());        

insert into account_tb
		(number, password, balance, user_id, created_at)
values('2222', '1234', 1100, 2, now());        

insert into account_tb
		(number, password, balance, user_id, created_at)
values('3333', '1234', 0, 3, now());

-- 1. 이체 내역을 기록
-- 1111번 계좌에서 2222번 계좌로 100원 이체
insert into history_tb(amount, w_balance, d_balance, w_account_id, d_account_id, created_at)
	values(100, 900, 1100, 1, 2, now());

-- 2. 가상 ATM 기기에서 출금
-- 1111 계좌에서 100원만 출금하는 히스토리 만들기
insert into history_tb(amount, w_balance, d_balance, w_account_id, d_account_id, created_at)
	values(100, 800, null, 1, null, now());


-- 3. 가상 ATM 기기에서 입금
-- 1111 계좌로 500원만 입금하는 히스토리 만들기
insert into history_tb(amount, w_balance, d_balance, w_account_id, d_account_id, created_at)
	values(500, null, 1300, null, 1, now());