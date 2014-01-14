function main(filename_train, filename_test, model_order)
% Autoregression model for all trips of one day.

    train = importdata(filename_train);
    test = importdata(filename_test);
    X_train = train(:, 2:end);
    Y_train = train(:, 1);
    X_test = test(:, 2:end);
    Y_test = test(:, 1);
    
    % Train
    w = X_train' * X_train \ X_train' * Y_train;
    
    % Train error
    Y_hat_train = X_train * w;
    train_rmse_our = sqrt(mean((Y_hat_train - Y_train) .^ 2));
    train_rmse_oba = sqrt(mean(([0;Y_train(1:end-1)] - Y_train) .^2));

    
    % Test error
    Y_hat_test = X_test * w;
    test_rmse_our = sqrt(mean((Y_hat_test - Y_test) .^ 2));
    test_rmse_oba = sqrt(mean(([0;Y_test(1:end-1)] - Y_test) .^2));
    
    % Print results
    fprintf('model order: %d\n', model_order);
    fprintf('coefficients:\n'); disp(w)
    fprintf('training RMSE OUR: %d\n', train_rmse_our);
    fprintf('test RMSE OUR: %d\n', test_rmse_our);
    fprintf('training RMSE OBA: %d\n', train_rmse_oba);
    fprintf('test RMSE OBA: %d\n', test_rmse_oba);

    
end