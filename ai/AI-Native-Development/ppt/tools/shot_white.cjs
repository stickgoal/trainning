const { chromium } = require('C:/Users/ThinkPad/.workbuddy/plugins/marketplaces/experts/plugins/ppt-implement/skills/ppt-implement/scripts/export/node_modules/playwright');
const path = require('path');

const DIST = 'D:/workspace/处理/trainning/ai/AI-Native-Development/ppt/frontend/dist-slides';
const OUT = 'D:/workspace/处理/trainning/ai/AI-Native-Development/ppt/shots/shots_white';
const fs = require('fs');
fs.mkdirSync(OUT, { recursive: true });

// (slide-i.html index, label)
const targets = [
  [0, 'cover'],
  [4, 'ch1_table'],
  [5, 'diagram01'],
  [6, 'code00_setup'],
  [10, 'dividerB'],
  [16, 'code_langgraph'],
  [24, 'diagram07_mcp'],
  [36, 'diagram12_enterprise'],
  [40, 'ending'],
];

(async () => {
  const browser = await chromium.launch({ channel: 'chrome' });
  const page = await browser.newPage({ viewport: { width: 1440, height: 810 }, deviceScaleFactor: 1 });
  await page.route('**/*', (route) => {
    const u = route.request().url();
    if (u.includes('fonts.googleapis.com') || u.includes('fonts.gstatic.com')) return route.abort();
    return route.continue();
  });
  for (const [idx, label] of targets) {
    const file = path.join(DIST, `slide-${idx}.html`);
    if (!fs.existsSync(file)) { console.log('MISS', file); continue; }
    await page.goto('file://' + file, { waitUntil: 'load' });
    await page.waitForTimeout(400);
    const out = path.join(OUT, `${String(idx).padStart(2,'0')}_${label}.png`);
    await page.screenshot({ path: out });
    console.log('shot', idx, label);
  }
  await browser.close();
  console.log('DONE');
})();
