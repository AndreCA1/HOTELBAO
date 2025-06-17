create table client (active bit not null, id bigint not null auto_increment, email varchar(255), name varchar(255), password varchar(255), phone varchar(255), primary key (id)) engine=InnoDB;
create table daily (client_id bigint not null, daily_date datetime(6), id bigint not null auto_increment, room_id bigint not null, primary key (id)) engine=InnoDB;
create table room (active bit not null, price float(23) not null, id bigint not null auto_increment, description TEXT, image_url varchar(255), primary key (id)) engine=InnoDB;
alter table client add constraint UKbfgjs3fem0hmjhvih80158x29 unique (email);
alter table daily add constraint FK6waaijurjce75g8gw2fa3masm foreign key (client_id) references client (id);
alter table daily add constraint FKpus8hx3ixpwh88v074a1ot8fy foreign key (room_id) references room (id);
