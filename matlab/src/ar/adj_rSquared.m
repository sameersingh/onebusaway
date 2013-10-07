function result = adj_rSquared(values_observed, values_modelled, numVars)
%ADJ_RSQAURED Reports the adjusted R-Square value

    numSamples = length(values_observed);

    rSquared = 1 - ...
        sum((values_observed - values_modelled) .^ 2) / ...
        sum((values_observed - mean(values_observed)) .^ 2);

    result = 1 - ((1 - rSquared) * (numSamples - 1) / ...
        (numSamples - numVars - 1));

end