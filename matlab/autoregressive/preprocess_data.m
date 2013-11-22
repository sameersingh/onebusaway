function [X,Y] = preprocess_data(filename, skip_plot)
    % preprocess the filename
    % it has hardcoded params, should be generic for all trips

    close all;
    
    if nargin < 2
        skip_plot = true;
    end
    
    % X = the original data 
    X = sortrows(importdata(filename), 1);
    
    % Y = the preprocessing result
    Y = X;
   
    % Cut begining and end of trip, due to pole problem
    Y = Y(Y(:,4) >= 100 & Y(:,4) <= 26000, :);
    
    % Upper and lower bound on sched deviation
    Y = Y(Y(:,5) <= 2000 & Y(:,5) >= -2000,:);
    
    % Delete trip instances with less than x or more than y positions
    % reported
    trip_instances_unique = unique(Y(:,2));
    [~, groupId] = ismember(Y(:,2), trip_instances_unique);
    positions_count = accumarray(groupId, Y(:,5), [], @length);
    trip_instances_to_remove = trip_instances_unique(positions_count < 10 | positions_count > 80);
    for i = 1:length(trip_instances_to_remove);
        % zero out this entry
        Y(Y(:,2) == trip_instances_to_remove(i), :) = 0;
    end
    % remove zero'd entries
    Y = Y(any(Y,2), :);
    
    if ~skip_plot
        % Some before/after figures
        figure;
        subplot(2,1,1); stem(X(:,1),X(:,5)); ylabel('Original');
        title('Sched-Dev values vs. Timestamp'); 
        subplot(2,1,2); stem(Y(:,1),Y(:,5)); ylabel('Preprocessed');

        figure;
        subplot(2,1,1); hist(X(:,4),10000); ylabel('Original');
        title('Distribution of reported distances');
        subplot(2,1,2); hist(Y(:,4),10000); ylabel('Preprocessed');

        figure;
        subplot(2,1,1); hist(X(:,5),10000); ylabel('Original');
        title('Distribution of reported sched-dev');
        subplot(2,1,2); hist(Y(:,5),10000); ylabel('Preprocessed');

        figure;
        subplot(2,1,1);
        [~, trip_instance_id] = ismember(X(:,2), unique(X(:,2)));
        hist(accumarray(trip_instance_id, X(:,5), [], @length), 1000); ylabel('Original');
        title('Distribution of samples per trip');
        subplot(2,1,2); 
        [~, trip_instance_id] = ismember(Y(:,2), unique(Y(:,2)));
        hist(accumarray(trip_instance_id, Y(:,5), [], @length), 1000); ylabel('Preprocessed');
    end
    
end

