import Head from 'next/head';
/**
 * Waiting page for OSOC application
 *
 * @remarks
 * Users will be sent to this page when they have a valid account, but it isn't enabled yet
 * by an administrator
 *
 * @returns Waiting Page
 */
const WaitPage = () => {
  return (
    <div className="h-screen bg-[url('../public/img/login.png')] bg-center">
      <Head>
        <title>Hold on...</title>
      </Head>
      <div
        className="lg:rounded-5xl relative top-1/2 m-auto flex w-11/12 max-w-md -translate-y-1/2 flex-col items-center
                      rounded-md bg-[#F3F3f3] px-4 py-4 text-center
                     md:w-11/12 lg:w-10/12 lg:max-w-7xl"
      >
        <header className="flex flex-row items-center justify-center gap-4 pb-5 lg:align-top">
          <h1 className="float-left text-3xl font-bold text-osoc-blue sm:text-4xl">
            Open Summer Of Code
          </h1>
          <img
            src="https://osoc.be/img/logo/logo-osoc-color.svg"
            className="hidden h-24 w-24 sm:inline-block md:h-24 md:w-24 lg:h-32 lg:w-32"
          />
        </header>

        <section>
          <h2 className="text-xl font-medium">
            Please wait while we enable your account ...
          </h2>
          <p className="text-sm opacity-60">
            We{"'"}ll let you know once this has been completed
          </p>
        </section>
      </div>
    </div>
  );
};

export default WaitPage;
