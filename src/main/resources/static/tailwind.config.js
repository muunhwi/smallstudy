/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["../templates/**/*.{html,js}", "../static/js/**/*.js",  "./node_modules/flowbite/**/*.js"],
  theme: {
      container: {
            center: true,
          },
      extend: {
        colors: {
                customBlue: '#BDD4E2',  // 배경색의 HEX 값
        },
      },
  },
  plugins: [ require('flowbite/plugin')],
}