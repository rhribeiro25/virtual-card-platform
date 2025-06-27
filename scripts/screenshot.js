const puppeteer = require('puppeteer');
const path = require('path');

(async () => {
  const browser = await puppeteer.launch({
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });

  const page = await browser.newPage();
  const filePath = 'file://' + path.resolve('docs/index.html');
  await page.goto(filePath, { waitUntil: 'networkidle0' });

  await page.setViewport({ width: 1280, height: 800 });
  await page.screenshot({ path: 'docs/coverage.png' });

  await browser.close();
})();
