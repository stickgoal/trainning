const { chromium } = require('C:/Users/ThinkPad/.workbuddy/plugins/marketplaces/experts/plugins/ppt-implement/skills/ppt-implement/scripts/export/node_modules/playwright');
const fs = require('fs');
const path = require('path');

const MERMAID_FILE = 'D:/workspace/处理/trainning/ai/AI-Native-Development/artifacts/mermaid-v10.min.js';
const MERMAID_SRC = fs.readFileSync(MERMAID_FILE, 'utf8');
const DIAG_DIR = 'D:/workspace/处理/trainning/ai/AI-Native-Development/diagrams';
const OUT_DIR = 'D:/workspace/处理/trainning/ai/AI-Native-Development/frontend/public/assets/images';

(async () => {
  fs.mkdirSync(OUT_DIR, { recursive: true });
  const files = fs.readdirSync(DIAG_DIR).filter(f => f.endsWith('.mmd')).sort();
  const browser = await chromium.launch({ channel: 'chrome' });
  const page = await browser.newPage({ deviceScaleFactor: 2 });

  for (const f of files) {
    const code = fs.readFileSync(path.join(DIAG_DIR, f), 'utf8');
    const outName = 'diagram-' + f.replace(/\.mmd$/, '.png');
    try {
      // 在空白页加载 mermaid（内联注入，避免 file:// 脚本限制）
      await page.goto('about:blank');
      await page.addScriptTag({ content: MERMAID_SRC });
      const svg = await page.evaluate(async (code) => {
        mermaid.initialize({
          startOnLoad: false,
          theme: 'default',
          fontFamily: '"Microsoft YaHei", "Noto Sans SC", sans-serif',
          flowchart: { useMaxWidth: true, htmlLabels: true },
        });
        const r = await mermaid.render('mmd-diagram', code);
        return r.svg;
      }, code);

      // 把 svg 放进页面，测量尺寸
      await page.setContent('<html><head><style>html,body{margin:0;padding:0;background:#ffffff}</style></head><body>' + svg + '</body></html>');
      const dims = await page.evaluate(() => {
        const s = document.querySelector('svg');
        const r = s.getBoundingClientRect();
        const vb = s.viewBox && s.viewBox.baseVal;
        return {
          w: vb && vb.width ? vb.width : r.width,
          h: vb && vb.height ? vb.height : r.height,
        };
      });
      const W = Math.max(10, Math.ceil(dims.w));
      const H = Math.max(10, Math.ceil(dims.h));
      await page.setViewportSize({ width: W, height: H });
      await page.waitForTimeout(100);
      await page.screenshot({ path: path.join(OUT_DIR, outName) });
      console.log('OK', f, '->', W + 'x' + H);
    } catch (e) {
      console.log('FAIL', f, e.message.split('\n')[0]);
    }
  }
  await browser.close();
  console.log('DONE');
})();
