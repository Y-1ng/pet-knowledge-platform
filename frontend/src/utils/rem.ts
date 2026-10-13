/**
 * rem 适配方案
 * - 手机端 (< 768px): 按 375 设计稿比例缩放
 * - 桌面端 (>= 768px): 固定基础大小，不再等比放大
 */
const baseSize = 37.5;

function setRem() {
  const width = document.documentElement.clientWidth;
  if (width >= 768) {
    // 桌面端固定 rem，让 Vant 组件保持合理大小
    document.documentElement.style.fontSize = baseSize * 1.2 + 'px';
  } else {
    // 手机端按比例缩放
    const scale = width / 375;
    const maxScale = 1.25;
    const actualScale = Math.min(scale, maxScale);
    document.documentElement.style.fontSize = baseSize * actualScale + 'px';
  }
}

setRem();

window.onresize = function () {
  setRem();
};
