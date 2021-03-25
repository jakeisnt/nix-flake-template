{ pkgs ? import <nixpkgs> { } }:

pkgs.stdenv.mkDerivation {
  name = "TODO";
  src = pkgs.lib.cleanSource ./.;
  preferLocalBuild = true;

  buildInputs = with pkgs; [ hello ];
}
