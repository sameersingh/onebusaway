function run_trip_mrv(X, model_order)
%RUN_TRIP_MRV Use most-recent value for predicting next sample in series.
% Note: model_order is used only for creating the datasets and for sake of
% keeping the datasets used between all methods consistent.

% Get and prepare dataset
[~, ~, test] =  create_ar_matrix(X, model_order);
test_truth = test(:, 1);
test_pred = test(:, 2); % Represents most-recent reading (i.e., time t-1)

% Get test error
test_error = mean((test_truth - test_pred) .^ 2);
test_rSquared = adj_rSquared(test_truth, test_pred, 0); 

% Print results
fprintf('test MSE: %d\n', test_error);
fprintf('test Adj R-Squared: %d\n', test_rSquared);

end