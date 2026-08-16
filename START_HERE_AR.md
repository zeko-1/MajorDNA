# تشغيل MajorDNA في VS Code

افتحي مجلد `MajorDNA_COMPLETE_NEW` كاملًا في VS Code.

## 1. إضافة مفتاح Groq

افتحي الملف `backend/.env` وضعي المفتاح بعد علامة المساواة:

```text
GROQ_API_KEY=ضعي_المفتاح_هنا
```

لا يحتاج النموذج إلى تنزيل على اللابتوب؛ Groq يشغّله عبر API. ملف `.env` مستبعد من Git لحماية المفتاح.

## 2. تشغيل الباك إند

في Terminal أول:

```powershell
cd backend
mvn spring-boot:run
```

اتركي النافذة تعمل. ظهور `Started Application` يعني أن الباك إند جاهز على المنفذ 8080.

## 3. تشغيل الفرونت إند

في Terminal ثانٍ:

```powershell
cd frontend
npm install
npm run dev -- --host 127.0.0.1 --port 5188 --strictPort
```

افتحي `http://127.0.0.1:5188`.

## 4. أول دخول

```text
نوع الحساب: Admin
Username: admin
Password: Admin123!
```

من صفحة Admin أنشئي حساب الطالب وحددي كلمة مرور مؤقتة. عند أول دخول للطالب سيُطلب منه تغييرها قبل استخدام النظام. لا يوجد حساب طالب تجريبي ولا نتائج جاهزة؛ النتيجة تظهر فقط بعد إكمال الاختبار.

## 5. JavaFX

بعد وجود تقرير حقيقي واحد على الأقل:

```powershell
cd javafx-module
mvn javafx:run
```

الاختبار الرئيسي يبدأ بـ46 سؤالًا موثق البنية، ويمكن للأدمن إضافة أسئلة أخرى. اختبار المسارات الثلاثة اسمه `Track Test`.
