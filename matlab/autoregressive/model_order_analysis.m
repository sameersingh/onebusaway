function model_order_analysis( filename )
   % function to determine the model order of the Autoregressive model
   % Based on Ali's code
   
   max_order = 40;
   plot_data = zeros(max_order, 4);
    for order = 1:max_order;
        fprintf('Running with model order: %d\n', order);
        [~, X] = preprocess_data(filename);
        [train, ~, validate] =  build_matrix(X, order);

        X_train = train(:, 2:end);
        Y_train = train(:, 1);
        X_val = validate(:, 2:end);
        Y_val = validate(:, 1);

        % Train
        w = X_train' * X_train \ X_train' * Y_train;

        % Train error
        Y_hat_train = X_train * w;
        train_rmse = sqrt(mean((Y_hat_train - Y_train) .^ 2));
        train_adj_rSquared = adj_rSquared(Y_train, Y_hat_train, order); 

        % Validation error
        Y_hat_val = X_val * w;
        val_rmse = sqrt(mean((Y_hat_val - Y_val) .^ 2));
        val_adj_rSquared = adj_rSquared(Y_val, Y_hat_val, order); 

        % Print results
        fprintf('model order: %d\n', order);
        fprintf('coefficients:\n'); disp(w)
        fprintf('training RMSE: %d\n', train_rmse);
        fprintf('training Adj R-Squared: %d\n', train_adj_rSquared);
        fprintf('test RMSE: %d\n', val_rmse);
        fprintf('test Adj R-Squared: %d\n', val_adj_rSquared);
        
        % store results
        plot_data(order, :) = [train_rmse, val_rmse, train_adj_rSquared, val_adj_rSquared];
    end
    
    fprintf('Results:\n');
    format long g;
    [val, order] = min(plot_data(:,1));
    fprintf('Min RMSE train: %d , order %d\n', val, order);
    [val, order] = max(plot_data(:,3));
    fprintf('Max Adj R-Squared train: %d , order %d\n', val, order);

    [val, order] = min(plot_data(:,2));
    fprintf('Min RMSE Validation: %d , order %d\n', val, order);
    [val, order] = max(plot_data(:,4));
    fprintf('Max Adj R-Squared Validation: %d , order %d\n', val, order);
    
    % Plots
    figure;
    subplot(2,1,1);
    plot(1:max_order, plot_data(:,1), 1:max_order, plot_data(:,2));
    legend('training', 'validation'); ylabel('RMSE');
    title('Model Order vs Training & Validation RMSEs');
    subplot(2,1,2);
    plot(1:max_order, plot_data(:,3), 1:max_order, plot_data(:,4));
    ylabel('Adj R-Squared'); xlabel('Model Order');

end

