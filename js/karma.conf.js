module.exports = function (config) {
    config.set({
        frameworks: ['mocha', 'chai'],
        files: ['test/*.js'],
        preprocessors: {
            'public/*.js': ['webpack'],
            'test/*.js': ['webpack'],
        },
        webpack: {
            mode: 'development'
        },
        browsers: ['ChromeHeadless'],
    });
};
