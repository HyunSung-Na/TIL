const vertical = document.querySelector(`.vertical`)
const horozontal = document.querySelector(`.horozontal`)
const target = document.querySelector(`.target`)
const tag = document.querySelector(`.tag`)

addEventListener(`load`, () => {
    const tagetRect = target.getBoundingClientRect();
    const tagetHalfWidth = tagetRect.width / 2;
    const tagetHalfHeight = tagetRect.height / 2;

    document.addEventListener(`mousemove`, event => {
        const x = event.clientX;
        const y = event.clientY;
        console.log(`${x} ${y}`);

        vertical.style.transform = `translateX(${x}px)`;
        horozontal.style.transform = `translateY(${y}px)`;
        target.style.transform = `translate(${x - tagetHalfWidth}px, 
        ${y - tagetHalfHeight}px)`;
        tag.style.transform = `translate(${x}px, ${y}px)`;
        tag.innerHTML = `${x}px, ${y}px`;
        tag.style.transform = `translate(${x + 20}px, ${y + 20}px)`;
    });
})