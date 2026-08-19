/**
 * Driver: convert the 41 white-theme slide HTMLs into a single .pptx
 * using the ppt-implement skill's html2pptx_official.js exporter.
 */
const path = require('path');
const fs = require('fs');
const os = require('os');

const EXPORT_DIR = 'C:/Users/ThinkPad/.workbuddy/plugins/marketplaces/experts/plugins/ppt-implement/skills/ppt-implement/scripts/export';
const html2pptx = require(path.join(EXPORT_DIR, 'html2pptx_official.js'));

const DIST = path.join(__dirname, '..', 'frontend', 'dist-slides');
const TMP = fs.mkdtempSync(path.join(os.tmpdir(), 'pptx-'));

const SLIDE_COUNT = 41;

async function main() {
  const PptxGenJS = require(path.join(EXPORT_DIR, 'node_modules', 'pptxgenjs'));
  const pptx = new PptxGenJS();

  // HTML body is 1440x810 px @96dpi => 15" x 8.4375", 16:9
  const W_IN = 1440 / 96;
  const H_IN = 810 / 96;
  pptx.defineLayout({ name: 'W15x84375', width: W_IN, height: H_IN });
  pptx.layout = 'W15x84375';

  let totalWarnings = 0;
  for (let i = 0; i < SLIDE_COUNT; i++) {
    const htmlFile = path.join(DIST, `slide-${i}.html`);
    if (!fs.existsSync(htmlFile)) {
      console.error(`✗ missing ${htmlFile}`);
      continue;
    }
    try {
      const { warnings } = await html2pptx(htmlFile, pptx, {
        tmpDir: TMP,
        isDebug: false,
      });
      totalWarnings += (warnings || []).length;
      console.log(`✓ slide-${i}.html -> pptx slide ${i + 1}/${SLIDE_COUNT}`);
    } catch (err) {
      console.error(`✗ slide-${i}.html failed: ${err.message}`);
    }
  }

  const outName = path.join(__dirname, '..', 'presentation.pptx');
  await pptx.writeFile({ fileName: outName });
  console.log(`\n🎉 wrote ${outName}`);
  console.log(`   slides: ${SLIDE_COUNT}, total warnings: ${totalWarnings}`);
}

main().catch((e) => {
  console.error('FATAL', e);
  process.exit(1);
});
