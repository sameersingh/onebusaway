function result = adj_rSquared(Y, Y_hat, numPredic)
% Reports the adjusted R-Square value

  samples = length(Y);
  rSquared = 1 - sum((Y - Y_hat) .^ 2) / sum((Y - mean(Y)) .^ 2);
  result = 1 - ((1 - rSquared) * (samples - 1) / (samples - numPredic - 1));

end