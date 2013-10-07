function run_trip_ar(X, model_order)
%RUN_TRIP_AR Autoregression model for a single trip.
% if model_order < 1, then report cross-validation results
% for choosing model order.

%% Init

if nargin < 2
    model_order = 0;
end

% Set maximum order to try (used only for cross-validation)
% NOTE: If max_order is too large s.t. there is insufficient data points;
% then an error will be thrown by function 'create_ar_matrix'. This could
% also occur for small values of max_order if there isn't enough trips with
% at last 'max_order + 1' samples.
max_order = 40;


%% Run

if model_order < 1;
    % Report cross validation results for choosing best model order
    plot_data = zeros(max_order, 4);
    for order = 1:max_order;
        fprintf('Running with model order: %d\n', order);
        
        % Get and prepare dataset
        [train, validate, ~] =  create_ar_matrix(X, order);
        train_resp = train(:, 1);
        train_input = train(:, 2:end);
        val_resp = validate(:, 1);
        val_input = validate(:, 2:end);
        
        % Train
        ar_coeff = pinv(train_input' * train_input) * train_input' * train_resp;
        fprintf('coefficients:\n'); disp(ar_coeff)
        
        % Get train error
        train_mse = mean(((train_input * ar_coeff) - train_resp) .^ 2);
        train_adj_rSquared = adj_rSquared(train_resp, train_input * ar_coeff, order); 
        
        % Get validation error
        val_mse = mean(((val_input * ar_coeff) - val_resp) .^ 2);
        val_adj_rSquared = adj_rSquared(val_resp, val_input * ar_coeff, order); 
        
        % store results
        plot_data(order, :) = [train_mse, val_mse, train_adj_rSquared, val_adj_rSquared];
    end
    
    % Dispaly results
    fprintf('RESULTS:\n');
    format long g;
    
    [val, order] = min(plot_data(:,1));
    fprintf('min training MSE: %d (order %d)\n', val, order);
    [val, order] = max(plot_data(:,3));
    fprintf('max training Adj R-Squared: %d (order %d)\n', val, order);

    [val, order] = min(plot_data(:,2));
    fprintf('min validation MSE: %d (order %d)\n', val, order);
    [val, order] = max(plot_data(:,4));
    fprintf('max validation Adj R-Squared: %d (order %d)\n', val, order);
    
    % Plot figures
    figure;
    subplot(2,1,1);
    plot(1:max_order, plot_data(:,1), 1:max_order, plot_data(:,2));
    legend('training error', 'validation error'); ylabel('MSE');
    title('Model order vs training and validation error');
    subplot(2,1,2);
    plot(1:max_order, plot_data(:,3), 1:max_order, plot_data(:,4));
    ylabel('Adj R-Squared'); xlabel('model order');
    
else
    % Train and report results on test set using provided model order.
    
    % Get and prepare dataset
    [train, ~, test] =  create_ar_matrix(X, model_order);
    train_resp = train(:, 1);
    train_input = train(:, 2:end);
    test_resp = test(:, 1);
    test_input = test(:, 2:end);
    
    % Train
    ar_coeff = pinv(train_input' * train_input) * train_input' * train_resp;
    
    % Get train error
    train_mse = mean(((train_input * ar_coeff) - train_resp) .^ 2);
    train_adj_rSquared = adj_rSquared(train_resp, train_input * ar_coeff, model_order); 
    
    % Get test error
    test_error = mean(((test_input * ar_coeff) - test_resp) .^ 2);
    test_rSquared = adj_rSquared(test_resp, test_input * ar_coeff, model_order); 
    
    % Print results
    fprintf('model order: %d\n', model_order);
    fprintf('coefficients:\n'); disp(ar_coeff)
    fprintf('training MSE: %d\n', train_mse);
    fprintf('training Adj R-Squared: %d\n', train_adj_rSquared);
    fprintf('test MSE: %d\n', test_error);
    fprintf('test Adj R-Squared: %d\n', test_rSquared);
    
end