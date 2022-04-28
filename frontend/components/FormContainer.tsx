import type { FC, PropsWithChildren } from 'react';

/**
 * Parameters for FormContainer Component
 * {@label FormContainerProps}
 *
 * @see PropsWithChildren
 */
type FormContainerProps = PropsWithChildren<{
  /**
   * Title of the page that is being displayed
   */
  pageTitle: string;
}>;

/**
 * Container for register and login forms
 *
 * @remarks
 * This is a container with 2 colums with 2 images on either side of a central form,
 * which is passed as a child component.
 *
 * {@label FormContainer}
 *
 * @see {@link FormContainerProps}
 */
const FormContainer: FC<FormContainerProps> = ({ pageTitle, children }) => {
  return (
    <>
      <div className="h-screen bg-[url('../public/img/login.png')] bg-center">
        {/* Left side images */}
        <div
          className="lg:rounded-5xl relative top-1/2 m-auto flex w-11/12 max-w-md -translate-y-1/2 flex-col items-center
                      rounded-md bg-[#F3F3f3] px-4 py-4 text-center
                     md:w-11/12 lg:grid lg:w-10/12 lg:max-w-7xl lg:grid-cols-2 lg:gap-2 xl:grid-cols-3"
        >
          <div className="hidden max-w-lg place-self-center xl:block">
            <img
              src="https://osoc.be/img/pictures/osoc17-1.jpg"
              alt="image of 4 people posing in front of a wall with post-its"
              className="object-scale-down shadow-sm shadow-gray-600 xl:mb-4"
            />
            <img
              src="https://i0.wp.com/blog.okfn.org/files/2018/08/image3.jpg?fit=1200%2C800&ssl=1"
              alt="Group of people cheering on OSOC"
              className="object-scale-down shadow-sm shadow-gray-600"
            />
          </div>
          {/* Main form component */}
          <div className="flex max-h-full max-w-full flex-col items-center justify-center">
            <header className="flex flex-row items-center justify-center gap-4 pb-5 lg:align-top">
              <h1 className="float-left text-3xl font-bold text-osoc-blue sm:text-4xl">
                {pageTitle}
              </h1>
              <img
                src="https://osoc.be/img/logo/logo-osoc-color.svg"
                className="hidden h-16 w-16 sm:inline-block md:h-24 md:w-24 lg:h-32 lg:w-32"
                alt="The OSOC logo"
              />
            </header>

            {children}
          </div>
          {/* Right side images */}
          <div className="hidden max-w-lg place-self-center lg:block">
            <img
              src="https://osoc.be/img/pictures/osoc17-2.jpg"
              alt="image of 4 people standing around a wall with post-its"
              className="object-scale-down shadow-sm shadow-gray-600 lg:mb-4"
            />
            <img
              src="https://osoc.be/img/pictures/osoc17-3.jpg"
              alt="image of someone trying to give you a fistbump"
              className="object-scale-down shadow-sm shadow-gray-600"
            />
          </div>
        </div>
      </div>
    </>
  );
};

export default FormContainer;
