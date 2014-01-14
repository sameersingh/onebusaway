function [train, test, valid] = build_matrix(X, model_order)
%   builds the autoregressive matrix, the first column of the matrix is the
%   target. For example, if order is 2, then each row will be: x(t) x(t-1) 
%   x(t-2). In AutoRegression, we try to solve: 
%   x(t)= x(t-1)w(1) + x(t-2)w(2) 

    dataset =  [];
    % get unique trip instances
    trip_instances_unique = unique(X(:,2)); 
    for trip_instance = 1:length(trip_instances_unique);
        % create time series
        trip_instance_serie = X(X(:,2) == trip_instances_unique(trip_instance), 5);
        for i = 0:(length(trip_instance_serie) - model_order - 1);
            dataset = [fliplr(trip_instance_serie(end-model_order-i:end-i)'); dataset];
        end
    end
    
    % Split
    total_datapoints = size(dataset,1);
    train_datapoints = round(total_datapoints * .5);
    valid_datapoints = round(total_datapoints * .25);
    test_datapoints = total_datapoints - train_datapoints - valid_datapoints;

    train = dataset(1:train_datapoints, :);
    valid = dataset(train_datapoints+1:train_datapoints+valid_datapoints, :);
    test = dataset(train_datapoints+valid_datapoints+1:end, :);

    format long g;
    fprintf('Total Size: %d\n', total_datapoints);
    fprintf('Train Size: %d\n', train_datapoints);
    fprintf('Validation Size: %d\n', valid_datapoints);
    fprintf('Test Size: %d\n', test_datapoints);
    
end