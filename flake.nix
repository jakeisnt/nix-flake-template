{
  description = "TODO";

  inputs = {
    nixpkgs.url = "nixpkgs/nixos-unstable";
    utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, utils, ... }:
    utils.lib.eachDefaultSystem (system:
      let
        inherit (lib) attrValues;
        pkgs = nixpkgs.legacyPackages.${system};
        lib = nixpkgs.lib;
      in rec {
        packages.hello = pkgs.hello;

        apps.hello = utils.lib.mkApp {
          drv = packages.hello;
        };

        defaultPackage = packages.hello;

        devShell = with pkgs; mkShell {
          buildInputs = [ ];
        };
      });
}
