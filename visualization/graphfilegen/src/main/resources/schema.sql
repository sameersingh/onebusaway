create table route ( 
       id int not null 
     , name varchar(20) not null 
     , agency_id varchar(20) not null
     , primary key (id)
); 
 
create table trip ( 
       id int not null 
     , route_id int not null
     , headsign varchar(50) not null
     , primary key (id) 
     , constraint fk_route_id foreign key (route_id) references route(id) 
); 

create table stop ( 
       id int not null 
     , name varchar(50) not null
     , lat varchar(20) not null
     , lon varchar(20) not null
     , primary key (id)
); 

create table stop_time (
        trip_id int not null
      , stop_id int not null
      , arrival_time varchar(20) not null
      , departure_time varchar(20) not null
      , stop_sequence int not null
      , primary key(trip_id, stop_id)
      , constraint fk_trip_id foreign key (trip_id) references trip(id)
      , constraint fk_stop_id foreign key (stop_id) references stop(id)
);

create table bus_position (
		timestamp bigint not null
	  , service_date bigint not null
	  , trip_id int not null
	  , distance_trip decimal not null
	  , sched_deviation decimal not null
	  , lat varchar(20) not null
	  , lon varchar(20) not null
	  , primary key(timestamp, service_date, trip_id)
      , constraint fk_trip_id foreign key (trip_id) references trip(id)

);	  