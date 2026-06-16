# 📚 EBOOK — Frontend

Aplicación web desarrollada con **Angular** como parte del proyecto Capstone. Permite a los usuarios acceder, visualizar y gestionar contenido de libros digitales.

---

## 🛠️ Tecnologías

- [Angular CLI](https://github.com/angular/angular-cli) v20.x
- TypeScript
- HTML5 / SCSS

---

## ⚙️ Requisitos previos

Asegúrate de tener instalado:

- [Node.js](https://nodejs.org/) (versión recomendada: 18.x o superior)
- [Angular CLI](https://angular.dev/tools/cli) instalado globalmente:

```bash
npm install -g @angular/cli
```

---

## 🚀 Instalación

Clona el repositorio e instala las dependencias:

```bash
git clone https://github.com/blazee14/EBOOK.git
cd EBOOK/FrontEnd
npm install
```

---

## 💻 Servidor de desarrollo

Para iniciar el servidor local:

```bash
ng serve
```

Luego abre tu navegador en: [http://localhost:4200](http://localhost:4200)

> La aplicación se recarga automáticamente al guardar cambios en los archivos.

---

## 🏗️ Build para producción

```bash
ng build
```

Los archivos compilados se generan en la carpeta `dist/`. La build de producción incluye optimizaciones de rendimiento.

---

## 🧩 Generar componentes

```bash
ng generate component nombre-componente
```

Para ver todos los esquemas disponibles:

```bash
ng generate --help
```

---

## 🧪 Pruebas

### Pruebas unitarias
```bash
ng test
```

### Pruebas end-to-end
```bash
ng e2e
```

---

## 🤝 Contribución (equipo)

1. Clona el repositorio
2. Crea tu rama de trabajo:
   ```bash
   git checkout -b tu-nombre/feature
   ```
3. Haz tus cambios y commitea:
   ```bash
   git add .
   git commit -m "descripción de los cambios"
   git push origin tu-nombre/feature
   ```
4. Abre un **Pull Request** hacia `main` para revisión

> Siempre hacer `git pull` antes de empezar a trabajar para tener el código actualizado.

---

## 📁 Estructura del proyecto

```
FrontEnd/
├── src/
│   ├── app/          # Componentes, servicios y módulos
│   ├── assets/       # Imágenes y recursos estáticos
│   └── environments/ # Configuración por entorno
├── public/
├── angular.json
└── package.json
```

---

## 📄 Recursos

- [Documentación de Angular](https://angular.dev)
- [Angular CLI Reference](https://angular.dev/tools/cli)
