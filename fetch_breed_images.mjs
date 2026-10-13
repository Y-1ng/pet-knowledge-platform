/**
 * 从 Wikipedia 抓取真正对应品种的图片
 * 通过 Wikipedia API 获取品种文章的配图
 *
 * 运行: node fetch_breed_images.mjs
 */
import https from 'https';
import http from 'http';
import fs from 'fs';
import path from 'path';

const IMG_DIR = 'C:/Users/ztk.LAPTOP-TNARNTNI/Desktop/pet-knowledge-platform/frontend/public/images/breeds';

// ===== 品种 → Wikipedia 搜索词映射 =====
// 用英文名搜英文维基，确保能找到准确的品种配图
const BREED_WIKI = {
  // 猫咪 (id 1-15)
  1:  { zh: '英国短毛猫', en: 'British Shorthair' },
  2:  { zh: '布偶猫', en: 'Ragdoll' },
  3:  { zh: '暹罗猫', en: 'Siamese cat' },
  4:  { zh: '波斯猫', en: 'Persian cat' },
  5:  { zh: '美国短毛猫', en: 'American Shorthair' },
  6:  { zh: '苏格兰折耳猫', en: 'Scottish Fold' },
  7:  { zh: '缅因猫', en: 'Maine Coon' },
  8:  { zh: '中华田园猫', en: 'Chinese Li Hua' },
  9:  { zh: '斯芬克斯猫', en: 'Sphynx cat' },
  10: { zh: '挪威森林猫', en: 'Norwegian Forest cat' },
  11: { zh: '异国短毛猫', en: 'Exotic Shorthair' },
  12: { zh: '孟加拉豹猫', en: 'Bengal cat' },
  13: { zh: '伯曼猫', en: 'Birman' },
  14: { zh: '俄罗斯蓝猫', en: 'Russian Blue' },
  15: { zh: '阿比西尼亚猫', en: 'Abyssinian cat' },

  // 狗狗 (id 16-33)
  16: { zh: '阿拉斯加雪橇犬', en: 'Alaskan Malamute' },
  17: { zh: '金毛寻回犬', en: 'Golden Retriever' },
  18: { zh: '拉布拉多寻回犬', en: 'Labrador Retriever' },
  19: { zh: '哈士奇', en: 'Siberian Husky' },
  20: { zh: '柯基犬', en: 'Pembroke Welsh Corgi' },
  21: { zh: '法国斗牛犬', en: 'French Bulldog' },
  22: { zh: '边境牧羊犬', en: 'Border Collie' },
  23: { zh: '德国牧羊犬', en: 'German Shepherd' },
  24: { zh: '萨摩耶犬', en: 'Samoyed dog' },
  25: { zh: '柴犬', en: 'Shiba Inu' },
  26: { zh: '贵宾犬', en: 'Poodle' },
  27: { zh: '英国斗牛犬', en: 'English Bulldog' },
  28: { zh: '博美犬', en: 'Pomeranian dog' },
  29: { zh: '吉娃娃', en: 'Chihuahua dog' },
  30: { zh: '雪纳瑞', en: 'Schnauzer' },
  31: { zh: '松狮犬', en: 'Chow Chow' },
  32: { zh: '秋田犬', en: 'Akita dog' },
  33: { zh: '中华田园犬', en: 'Chinese Rural Dog' },

  // 兔子 (id 34-40)
  34: { zh: '荷兰垂耳兔', en: 'Holland Lop' },
  35: { zh: '荷兰侏儒兔', en: 'Netherland Dwarf rabbit' },
  36: { zh: '安哥拉兔', en: 'Angora rabbit' },
  37: { zh: '狮子兔', en: 'Lionhead rabbit' },
  38: { zh: '雷克斯兔', en: 'Rex rabbit' },
  39: { zh: '海棠兔', en: 'Hotot rabbit' },
  40: { zh: '迷你雷克斯兔', en: 'Mini Rex rabbit' },

  // 鸟类 (id 41-48)
  41: { zh: '虎皮鹦鹉', en: 'Budgerigar' },
  42: { zh: '玄凤鹦鹉', en: 'Cockatiel' },
  43: { zh: '牡丹鹦鹉', en: 'Lovebird' },
  44: { zh: '金丝雀', en: 'Canary' },
  45: { zh: '百灵鸟', en: 'Skylark' },
  46: { zh: '画眉鸟', en: 'Chinese Hwamei' },
  47: { zh: '非洲灰鹦鹉', en: 'African Grey Parrot' },
  48: { zh: '文鸟', en: 'Java Sparrow' },

  // 鱼类 (id 49-56)
  49: { zh: '金鱼', en: 'Goldfish' },
  50: { zh: '锦鲤', en: 'Koi' },
  51: { zh: '孔雀鱼', en: 'Guppy' },
  52: { zh: '神仙鱼', en: 'Angelfish' },
  53: { zh: '斗鱼', en: 'Betta fish' },
  54: { zh: '红绿灯鱼', en: 'Neon tetra' },
  55: { zh: '龙鱼', en: 'Asian arowana' },
  56: { zh: '七彩神仙鱼', en: 'Discus fish' },

  // 爬宠 (id 57-65)
  57: { zh: '巴西龟', en: 'Red-eared slider' },
  58: { zh: '中华草龟', en: 'Chinese pond turtle' },
  59: { zh: '辐射陆龟', en: 'Radiated tortoise' },
  60: { zh: '鬃狮蜥', en: 'Bearded dragon' },
  61: { zh: '豹纹守宫', en: 'Leopard gecko' },
  62: { zh: '玉米蛇', en: 'Corn snake' },
  63: { zh: '赫曼陆龟', en: "Hermann's tortoise" },
  64: { zh: '绿鬣蜥', en: 'Green iguana' },
  65: { zh: '角蛙', en: 'Ceratophrys' },
};

// 品种ID → 分类目录映射
function getCategoryDir(breedId) {
  if (breedId <= 15) return 'cat';
  if (breedId <= 33) return 'dog';
  if (breedId <= 40) return 'rabbit';
  if (breedId <= 48) return 'bird';
  if (breedId <= 56) return 'fish';
  return 'reptile';
}

// 计算当前分类中的序号
function getIndexInCategory(breedId) {
  if (breedId <= 15) return breedId;
  if (breedId <= 33) return breedId - 15;
  if (breedId <= 40) return breedId - 33;
  if (breedId <= 48) return breedId - 40;
  if (breedId <= 56) return breedId - 48;
  return breedId - 56;
}

// HTTP GET 帮助函数
function httpGet(url) {
  return new Promise((resolve, reject) => {
    const protocol = url.startsWith('https') ? https : http;
    protocol.get(url, { headers: { 'User-Agent': 'PetKnowledge/1.0' } }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        if (res.statusCode >= 200 && res.statusCode < 300) resolve(data);
        else reject(new Error(`HTTP ${res.statusCode}`));
      });
    }).on('error', reject);
  });
}

// 下载图片
function downloadImage(url, dest) {
  return new Promise((resolve, reject) => {
    https.get(url, { headers: { 'User-Agent': 'PetKnowledge/1.0' } }, (res) => {
      // 跟随重定向
      if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
        const redirect = res.headers.location.startsWith('http')
          ? res.headers.location
          : `https:${res.headers.location}`;
        downloadImage(redirect, dest).then(resolve).catch(reject);
        return;
      }
      if (res.statusCode !== 200) {
        reject(new Error(`HTTP ${res.statusCode}`));
        return;
      }
      const file = fs.createWriteStream(dest);
      res.pipe(file);
      file.on('finish', () => { file.close(); resolve(true); });
      file.on('error', reject);
    }).on('error', reject);
  });
}

// 从 Wikipedia 获取品种图片
async function fetchWikipediaImage(searchTerm, lang = 'en') {
  const apiUrl = `https://${lang}.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(searchTerm)}&format=json&srlimit=1&srwhat=text`;

  try {
    const data = await httpGet(apiUrl);
    const json = JSON.parse(data);
    const pages = json.query?.search;
    if (!pages || pages.length === 0) return null;

    const pageTitle = pages[0].title;

    // 获取页面主图片
    const imageUrl = `https://${lang}.wikipedia.org/w/api.php?action=query&titles=${encodeURIComponent(pageTitle)}&prop=pageimages&format=json&pithumbsize=500`;
    const imgData = await httpGet(imageUrl);
    const imgJson = JSON.parse(imgData);

    const pagesObj = imgJson.query?.pages;
    if (!pagesObj) return null;

    const pageId = Object.keys(pagesObj)[0];
    const thumbnail = pagesObj[pageId]?.thumbnail?.source;
    return thumbnail || null;
  } catch (e) {
    return null;
  }
}

async function main() {
  let ok = 0, fail = 0, skip = 0;

  for (const [idStr, names] of Object.entries(BREED_WIKI)) {
    const id = parseInt(idStr);
    const dir = getCategoryDir(id);
    const idx = getIndexInCategory(id);
    const dest = path.join(IMG_DIR, dir, `${idx}.jpg`);

    // 跳过已存在且有效的图片
    if (fs.existsSync(dest) && fs.statSync(dest).size > 5000) {
      skip++;
      continue;
    }

    process.stdout.write(`[${dir} ${idx}] ${names.zh}... `);

    let imgUrl = null;

    // 先试英文 Wikipedia
    if (names.en) {
      imgUrl = await fetchWikipediaImage(names.en, 'en');
    }

    // 英文章没有图则试中文
    if (!imgUrl) {
      imgUrl = await fetchWikipediaImage(names.zh, 'zh');
    }

    // 有图则下载
    if (imgUrl) {
      try {
        await downloadImage(imgUrl, dest);
        const size = fs.statSync(dest).size;
        if (size > 3000) {
          console.log(`✅ ${(size/1024).toFixed(0)}KB`);
          ok++;
        } else {
          console.log(`❌ 图片太小 (${size}B)`);
          fs.unlinkSync(dest);
          fail++;
        }
      } catch (e) {
        console.log(`❌ 下载失败: ${e.message}`);
        fail++;
      }
    } else {
      console.log(`❌ 未找到图片`);
      fail++;
    }

    // 请求延迟
    await new Promise(r => setTimeout(r, 600));
  }

  console.log(`\n📊 总计: ${ok} 成功, ${fail} 失败, ${skip} 跳过`);
}

main().catch(console.error);
