function main(filename, model_order)
% Autoregression model for a single trip.
% Based on Ali's code
    [~, X] = preprocess_data(filename);
    [train, test] =  build_matrix(X, model_order);
    X_train = train(:, 2:end);
    Y_train = train(:, 1);
    X_test = test(:, 2:end);
    Y_test = test(:, 1);
    
    % Train
    w = X_train' * X_train \ X_train' * Y_train;
    
    % Train error
    Y_hat_train = X_train * w;
    train_rmse = sqrt(mean((Y_hat_train - Y_train) .^ 2));
    train_adj_rSquared = adj_rSquared(Y_train, Y_hat_train, model_order); 
    
    % Test error
    Y_hat_test = X_test * w;
    test_rmse = sqrt(mean((Y_hat_test - Y_test) .^ 2));
    test_adj_rSquared = adj_rSquared(Y_test, Y_hat_test, model_order); 
    
    % Print results
    fprintf('model order: %d\n', model_order);
    fprintf('coefficients:\n'); disp(w)
    fprintf('training RMSE: %d\n', train_rmse);
    fprintf('training Adj R-Squared: %d\n', train_adj_rSquared);
    fprintf('test RMSE: %d\n', test_rmse);
    fprintf('test Adj R-Squared: %d\n', test_adj_rSquared);
    
end