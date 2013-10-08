create table route ( 
       id int not null 
     , name varchar(80) not null 
     , agency_id varchar(50) not null
     , primary key (id, agency_id)
); 
 
create table trip ( 
       id int not null 
     , route_id int not null 
     , unique uq_id_route_id (id, route_id) 
     , primary key (id) 
     , constraint fk_route_id foreign key (route_id) references route(id) 
); 

create table stop ( 
       id int not null 
     , name varchar(80) not null 
     , primary key (id)
); 

create table stop_time (
        trip_id int not null
      , stop_id int not null
      , stop_sequence int not null
      , primary key(trip_id, stop_id, stop_sequence)
      , constraint fk_trip_id foreign key (trip_id) references trip(id)
      , constraint fk_stop_id foreign key (stop_id) references stop(id)
);