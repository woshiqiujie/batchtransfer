select *from payment_wallets;
select *from receiver_info;
select *from batch_transfer_summary;


select *from payment_wallets where payment_wallets.environment = '01'and transfer_type = '00';
select *from receiver_info where environment = '01'and transfer_type = '01';


ALTER USER postgres WITH PASSWORD 'qwer1234';


show   payment_wallets; -- 查看表的结构

DROP TABLE IF EXISTS public.payment_wallets;
DROP TABLE IF EXISTS public.receiver_info;
DROP TABLE IF EXISTS public.batch_transfer_summary;


-- 支付钱包表
CREATE TABLE public.payment_wallets (
                                        wallet_id_account VARCHAR(60) PRIMARY KEY,  -- 钱包ID/账户
                                        wallet_account_name VARCHAR(60),  -- 钱包/账户名称
                                        contract_number VARCHAR(34),  -- 签约协议号
                                        bank_code VARCHAR(14),  -- 合作银行编号
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
                                        transfer_type VARCHAR(2) NOT NULL,  -- 转出方类型 (00-对公钱包, 01-对公账户)
                                        environment VARCHAR(2) NOT NULL,  -- 环境 (01-定版, 02-预演)
                                        remarks VARCHAR(60) NOT NULL  -- 备注
);
---收款钱包表
CREATE TABLE public.receiver_info (
                                      transfer_type character varying(2) NOT NULL,  -- 转入方类型
                                      receiver_id character varying(19) NOT NULL,  -- 收款钱包ID/手机号/身份证号
                                      user_name character varying(60) NOT NULL,  -- 用户名称
                                      created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
                                      CONSTRAINT receiver_info_pkey PRIMARY KEY(receiver_id),  -- 主键为收款账户ID
                                      environment VARCHAR(2) NOT NULL,  -- 环境 (01-定版, 02-预演)
                                      remarks VARCHAR(60) NOT NULL  -- 备注
);


-- 添加中文注释
COMMENT ON TABLE public.payment_wallets IS '支付钱包表';
COMMENT ON COLUMN public.payment_wallets.wallet_id_account IS '钱包ID/账户';
COMMENT ON COLUMN public.payment_wallets.wallet_account_name IS '钱包/账户名称';
COMMENT ON COLUMN public.payment_wallets.contract_number IS '签约协议号';
COMMENT ON COLUMN public.payment_wallets.bank_code IS '合作银行编号';
COMMENT ON COLUMN public.payment_wallets.created_at IS '创建时间';
COMMENT ON COLUMN public.payment_wallets.transfer_type IS '转出方类型 (00-对公钱包, 01-对公账户)';
COMMENT ON COLUMN public.payment_wallets.environment IS '环境 (01-定版, 02-预演)';
COMMENT ON COLUMN public.payment_wallets.remarks IS '备注';



--收款钱包表
CREATE TABLE public.receiver_info (
                                      transfer_type character varying(2) NOT NULL,  -- 转入方类型
                                      receiver_id character varying(19) NOT NULL,  -- 收款钱包ID/手机号/身份证号
                                      user_name character varying(60) NOT NULL,  -- 用户名称
                                      created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
                                      CONSTRAINT receiver_info_pkey PRIMARY KEY(receiver_id),  -- 主键为收款账户ID
                                      environment VARCHAR(2) NOT NULL,  -- 环境 (01-定版, 02-预演)
                                      remarks VARCHAR(60) NOT NULL  -- 备注
);



ALTER TABLE payment_wallets
    ADD COLUMN environment VARCHAR(2) DEFAULT '01' NOT NULL CHECK (environment IN ('01', '02')),
    ADD COLUMN remarks VARCHAR(60);




ALTER TABLE receiver_info
    ADD COLUMN environment VARCHAR(2) DEFAULT '01' NOT NULL CHECK (environment IN ('01', '02')),
    ADD COLUMN remarks VARCHAR(60);

COMMENT ON COLUMN public.receiver_info.environment IS '环境 (01-定版, 02-预演)';
COMMENT ON COLUMN public.receiver_info.remarks IS '备注';






ALTER TABLE payment_wallets
    ALTER COLUMN contract_number TYPE VARCHAR(34);



INSERT INTO payment_wallets (wallet_id_account, wallet_account_name, contract_number, bank_code, transfer_type)
VALUES ('walqqqq1234415', 'Wallet One', '123456789012345678901234567890123', '12345678901234', '01');




CREATE TABLE batch_transfer_summary (
                                        batch_no VARCHAR(35) PRIMARY KEY,                -- 批次号，主键，格式为 yyyyMMdd + 2位银银渠道号 + 合作银行编号 + 自定义序列，总长度不超过35，或自定义格式保持唯一性
                                        payer_wallet VARCHAR(16) NOT NULL,               -- 付款方钱包，转出方钱包ID，仅支持邮储对公钱包
                                        payer_wallet_name VARCHAR(60) NOT NULL,          -- 付款方钱包名称
                                        total_transactions INT NOT NULL,                 -- 总交易笔数，整数
                                        total_amount DECIMAL(22, 2) NOT NULL,            -- 总金额，保留小数点后2位
                                        payer_contract_no VARCHAR(34) NOT NULL,         -- 付款方签约协议号
                                        partner_bank_code VARCHAR(14) NOT NULL,         -- 合作银行编号，长度不超过14个字符
                                        business_type VARCHAR(3) NOT NULL,              -- 业务类型，代码标识
                                        business_category VARCHAR(8) NOT NULL,          -- 业务种类，代码标识
                                        environment VARCHAR(2) CHECK (environment IN ('01', '02')) NOT NULL,  -- 环境字段，01-定版 或 02-预演
                                        center_flag VARCHAR(2) CHECK (center_flag IN ('01', '02')) NOT NULL, -- 中心标志，01-亦庄 或 02-合肥
                                        file_type VARCHAR(2) CHECK (file_type IN ('03', '07')) NOT NULL,      -- 文件类型，03-批量转账 或 07-批量代发
                                        file_name VARCHAR(100),                         -- 文件名称
                                        resp_status VARCHAR(1) CHECK (resp_status IN ('0', '1', '3')) DEFAULT '3', -- 文件处理状态，0-成功，1-失败，3-处理中，默认为处理中
                                        creation_date DATE DEFAULT CURRENT_DATE,         -- 创建日期，系统自动生成
                                        creation_time TIME DEFAULT CURRENT_TIME         -- 创建时间，系统自动生成
);


-- 修改表注释
COMMENT ON TABLE batch_transfer_summary IS '批量转账记录表，记录每个批次的基本信息，包括付款方、收款方、金额、业务类型等';

-- 修改列注释
COMMENT ON COLUMN batch_transfer_summary.batch_no IS '批次号，主键，格式为 yyyyMMdd + 2位银银渠道号 + 合作银行编号 + 自定义序列，总长度不超过35，或自定义格式保持唯一性';
COMMENT ON COLUMN batch_transfer_summary.payer_wallet IS '付款方钱包，转出方钱包ID，仅支持邮储对公钱包';
COMMENT ON COLUMN batch_transfer_summary.payer_wallet_name IS '付款方钱包名称';
COMMENT ON COLUMN batch_transfer_summary.total_transactions IS '总交易笔数，整数';
COMMENT ON COLUMN batch_transfer_summary.total_amount IS '总金额，保留小数点后2位';
COMMENT ON COLUMN batch_transfer_summary.payer_contract_no IS '付款方签约协议号';
COMMENT ON COLUMN batch_transfer_summary.partner_bank_code IS '合作银行编号，长度不超过14个字符';
COMMENT ON COLUMN batch_transfer_summary.business_type IS '业务类型，代码标识';
COMMENT ON COLUMN batch_transfer_summary.business_category IS '业务种类，代码标识';
COMMENT ON COLUMN batch_transfer_summary.environment IS '环境字段，01-定版 或 02-预演';
COMMENT ON COLUMN batch_transfer_summary.center_flag IS '中心标志，01-亦庄 或 02-合肥';
COMMENT ON COLUMN batch_transfer_summary.file_type IS '文件类型，03-批量转账 或 07-批量代发';
COMMENT ON COLUMN batch_transfer_summary.file_name IS '文件名称';
COMMENT ON COLUMN batch_transfer_summary.resp_status IS '文件处理状态，0-成功，1-失败，3-处理中，默认为处理中';
COMMENT ON COLUMN batch_transfer_summary.creation_date IS '创建日期，系统自动生成';
COMMENT ON COLUMN batch_transfer_summary.creation_time IS '创建时间，系统自动生成';


ALTER TABLE batch_transfer_summary
    ALTER COLUMN creation_date TYPE DATE USING creation_date::DATE;


ALTER TABLE batch_transfer_summary
    ALTER COLUMN creation_time TYPE DATE USING creation_time::timestamp;

ALTER TABLE batch_transfer_summary
    ALTER COLUMN creation_time TYPE TIMESTAMP USING creation_time::TIMESTAMP,
    ALTER COLUMN creation_time SET DEFAULT CURRENT_TIMESTAMP;


ALTER TABLE batch_transfer_summary
    ADD COLUMN creation_time_new TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP;

UPDATE batch_transfer_summary
SET creation_time_new = CURRENT_DATE + creation_time;

ALTER TABLE batch_transfer_summary
    DROP COLUMN creation_time;

ALTER TABLE batch_transfer_summary
    RENAME COLUMN creation_time_new TO creation_time;

-- 修改 creation_time 列的数据类型为 TIMESTAMP 并设置默认值为 CURRENT_TIMESTAMP
ALTER TABLE batch_transfer_summary
    ALTER COLUMN creation_time TYPE TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE batch_transfer_summary
    ALTER COLUMN creation_time SET DEFAULT CURRENT_TIMESTAMP;
