function [X,Y] = prepare_data(filename)
    %PREPARE_DATA(FILENAME)
    %  Imports data from the specified data file and pre-processes.
    % TODO: make general for all trips; right now operates off fixed
    % parameters.

    close all;
    
    %% Let X be the original data
    X = sortrows(importdata(filename), 1);
    
    %% Let Y be preprocessing result
    Y = X;
   
    % Remove begining/end of each trip
    Y = Y(Y(:,4) >= 100 & Y(:,4) <= 26200, :);
    
    % Impose upper + lower bounds on sched deviation
    Y = Y(Y(:,5) <= 2000 & Y(:,5) >= -2000,:);
    
    % Trim first couple trip instances - first couple entries are odd
    % NOTE: obviously specific to this trip
    Y = Y(Y(:,1) > 1.3345e12, :);
    
    % Remove trips with fewer than x samples or greater than y samples
    tripsUnique = unique(Y(:,2)); 
    [~, groupId] = ismember(Y(:,2), tripsUnique);
    tripsCount = accumarray(groupId, Y(:,5), [], @length);
    tripsToRemove = tripsUnique(tripsCount < 10 | tripsCount > 80);
    for i = 1:length(tripsToRemove);
        Y(Y(:,2) == tripsToRemove(i), :) = 0; % zero out this entry
    end
    Y = Y(any(Y,2), :); % remove zero'd entries
    
    % Squash dynamic range
    %Y(:,5) = sign(Y(:,5)) .* log(abs(Y(:,5) ./ 60) + 1);
    
    % Some before/after figures
    figure;
    subplot(2,1,1); stem(X(:,1),X(:,5)); ylabel('Original');
    title('Sched-Dev values vs. timestamp over all trips'); 
    subplot(2,1,2); stem(Y(:,1),Y(:,5)); ylabel('Preprocessed');
    
    figure;
    subplot(2,1,1); hist(X(:,4),10000); ylabel('Original');
    title('Distribution of reported distances over all trips');
    subplot(2,1,2); hist(Y(:,4),10000); ylabel('Preprocessed');
    
    figure;
    subplot(2,1,1); hist(X(:,5),10000); ylabel('Original');
    title('Distribution of reported sched-dev values over all trips');
    subplot(2,1,2); hist(Y(:,5),10000); ylabel('Preprocessed');
    
    figure; 
    subplot(2,1,1);
    [~, tripIdInt] = ismember(X(:,2), unique(X(:,2)));
    hist(accumarray(tripIdInt, X(:,5), [], @length), 1000); ylabel('Original');
    title('Distribution of samples per trip, over all trips');
    subplot(2,1,2); 
    [~, tripIdInt] = ismember(Y(:,2), unique(Y(:,2)));
    hist(accumarray(tripIdInt, Y(:,5), [], @length), 1000); ylabel('Preprocessed');
    
end

