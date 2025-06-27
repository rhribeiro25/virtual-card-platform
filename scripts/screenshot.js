const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch({
    executablePath: '/usr/bin/google-chrome', // Caminho padrão no Ubuntu
    headless: true,
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });

  const page = await browser.newPage();

  await page.goto(`file://${process.cwd()}/../target/site/jacoco/index.html`, {
    waitUntil: 'networkidle0'
  });

  await page.setViewport({ width: 1280, height: 800 });

  await page.screenshot({
    path: '../src/main/resources/static/docs/images/coverage/coverage.png',
    fullPage: true
  });

  await browser.close();
})();
