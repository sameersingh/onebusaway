function [train, validate, test] = create_ar_matrix(X, model_order)
%CREATE_AR_MATRIX create the autoregressive matrix, where the FIRST column
%   of matrix is the label (i.e. response) value. For example, if order is
%   3, then each row will be: x(t) x(t-1) x(t-2) x(t-3). In AR, we use such
%   rows to solve the equation: x(t)= x(t-1)a(1) + x(t-2)a(2) + x(t-3)a(3). 

    if model_order < 1
         return;
    end
    
    dataset =  [];
    tripsUnique = unique(X(:,2)); 
    for trip = 1:length(tripsUnique);
        tripSeries = X(X(:,2) == tripsUnique(trip), 5); % timeseries, vector
        for i = 0:(length(tripSeries) - model_order - 1);
            dataset = [fliplr(tripSeries(end-model_order-i : end-i)'); dataset];
        end
    end
    
    % Split
    numTotal = size(dataset,1);
    numTrain = round(numTotal * .5);
    numValidate = round(numTotal * .25);
    numTest = numTotal - numTrain - numValidate;
    if numTrain < model_order + 1 || ...
            numValidate < model_order + 1 || ...
            numTest < model_order + 1; 
        error('Not enough data or bad model order.');
    end
    
    train = dataset(1:numTrain, :);
    validate = dataset(numTrain+1:numTrain+numValidate, :);
    test = dataset(numTrain+numValidate+1:end, :);

    format long g;
    fprintf('total size: %d\n', numTotal);
    fprintf('train size: %d\n', numTrain);
    fprintf('validate size: %d\n', numValidate);
    fprintf('test size: %d\n', numTest);
    
end