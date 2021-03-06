var path = require('path');

module.exports = {
    entry: './static_resources/static/js/app.js',
    devtool: 'sourcemaps',
    cache: true,
    mode: 'development',
    output: {
        filename: './static_resources/static/built/bundle.js',
        path: path.resolve(__dirname, 'dist'),
    },
    module: {
        rules: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"]
                    }
                }]
            }
        ]
    }
};