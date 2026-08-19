const { chromium } = require('C:/Users/ThinkPad/.workbuddy/plugins/marketplaces/experts/plugins/ppt-implement/skills/ppt-implement/scripts/export/node_modules/playwright');
const fs = require('fs');
const path = require('path');

(async () => {
  const dir = 'D:/workspace/处理/trainning/ai/AI-Native-Development/ppt/frontend/dist-slides';
  const out = 'D:/workspace/处理/trainning/ai/AI-Native-Development/ppt/shots/shots';
  fs.mkdirSync(out, { recursive: true });
  const browser = await chromium.launch({ channel: 'chrome' });
  const page = await browser.newPage({ viewport: { width: 1440, height: 810 } });
  await page.route('**/*', route => {
    if (route.request().url().includes('fonts.googleapis') || route.request().url().includes('fonts.gstatic')) route.abort();
    else route.continue();
  });
  const files = fs.readdirSync(dir).filter(f => /^slide-\d+\.html$/.test(f)).sort((a,b) => parseInt(a.match(/\d+/)[0]) - parseInt(b.match(/\d+/)[0]));
  for (const f of files) {
    const n = parseInt(f.match(/\d+/)[0]);
    await page.goto('file://' + path.join(dir, f));
    await page.waitForTimeout(150);
    await page.screenshot({ path: path.join(out, `slide-${n}.png`) });
  }
  await browser.close();
  console.log('done', files.length);
})();
