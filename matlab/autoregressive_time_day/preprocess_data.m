function [X,Y] = preprocess_data(filename, skip_plot)
    % preprocess the filename
    % it has hardcoded params, should be generic for all trips

    close all;
    
    if nargin < 2
        skip_plot = true;
    end
    
    % X = the original data
    f = importdata(filename);
    X = sortrows(f, 1);
    
    % Y = the preprocessing result
    Y = X;
   
    % Cut begining of trip, due to pole problem. If there's problem at the
    % end, it should be captured by the sched deviation threshold
    Y = Y(Y(:,4) >= 200, :);
    
    % Upper and lower bound on sched deviation
    Y = Y(Y(:,5) <= 2000 & Y(:,5) >= -2000,:);
    
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
    end
    
    dlmwrite('data/perDayPreprocessed.dat',Y, 'delimiter', '\t', 'precision', 10)
    uniqueTrips = unique(Y(:,3));
    dlmwrite('data/uniqueTrips.dat',uniqueTrips, '\t')
    
end

