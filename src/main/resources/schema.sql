create table customer
(
    id       BIGINT AUTO_INCREMENT  PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    password VARCHAR(32) NOT NULL
);

create table asset
(
    id          BIGINT AUTO_INCREMENT  PRIMARY KEY,
    name        VARCHAR(32) NOT NULL,
    size        DECIMAL     NOT NULL,
    usable_size DECIMAL     NOT NULL,
    update_date TIMESTAMP   NOT NULL,
    customer_id BIGINT      NOT NULL,
    CONSTRAINT unique_asset_name_per_customer UNIQUE (name, customer_id)
);

alter table asset add foreign key (customer_id) references customer(id);

create table customer_order
(
    id          BIGINT AUTO_INCREMENT  PRIMARY KEY,
    order_side  VARCHAR(32) NOT NULL,
    size        DECIMAL     NOT NULL,
    price       DECIMAL     NOT NULL,
    status      VARCHAR(32) NOT NULL,
    create_date TIMESTAMP   NOT NULL,
    asset_name  VARCHAR(32) NOT NULL,
    customer_id BIGINT NOT NULL,
    CONSTRAINT fk_customer_order_asset FOREIGN KEY (asset_name, customer_id) REFERENCES asset(name, customer_id)
);


