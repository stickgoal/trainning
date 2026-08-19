const { chromium } = require('C:/Users/ThinkPad/.workbuddy/plugins/marketplaces/experts/plugins/ppt-implement/skills/ppt-implement/scripts/export/node_modules/playwright');
const path = require('path');
const fs = require('fs');
const DIST = 'D:/workspace/处理/trainning/ai/AI-Native-Development/ppt/frontend/dist-slides';
const OUT = 'D:/workspace/处理/trainning/ai/AI-Native-Development/ppt/shots/shots_white';
fs.mkdirSync(OUT, { recursive: true });

(async () => {
  const browser = await chromium.launch({ channel: 'chrome' });
  const page = await browser.newPage({ viewport: { width: 1440, height: 810 }, deviceScaleFactor: 1 });
  await page.route('**/*', r => (r.request().url().includes('fonts.googleapis.com') || r.request().url().includes('fonts.gstatic.com')) ? r.abort() : r.continue());
  for (const [idx, label] of [[10, 'dividerB']]) {
    await page.goto('file://' + path.join(DIST, `slide-${idx}.html`), { waitUntil: 'load' });
    await page.waitForTimeout(400);
    await page.screenshot({ path: path.join(OUT, `${String(idx).padStart(2, '0')}_${label}_v2.png`) });
    console.log('shot', idx, label);
  }
  await browser.close();
})();
